package com.asanrezerv.restaurantservice.service;

import com.asanrezerv.restaurantservice.client.MediaClient;
import com.asanrezerv.restaurantservice.client.SubscriptionClient;
import com.asanrezerv.restaurantservice.dto.branch.BranchDto;
import com.asanrezerv.restaurantservice.dto.branch.request.BranchRequest;
import com.asanrezerv.restaurantservice.dto.clients.subscription.BranchQuotaUsage;
import com.asanrezerv.restaurantservice.dto.clients.subscription.QuotaLimits;
import com.asanrezerv.restaurantservice.dto.clients.subscription.RestaurantQuotaUsage;
import com.asanrezerv.restaurantservice.dto.kafka.BranchEventDto;
import com.asanrezerv.restaurantservice.dto.kafka.TableCreationEvent;
import com.asanrezerv.restaurantservice.dto.restaurant.RestaurantDto;
import com.asanrezerv.restaurantservice.dto.restaurant.request.RestaurantRequest;
import com.asanrezerv.restaurantservice.dto.table.TableDto;
import com.asanrezerv.restaurantservice.dto.table.TableRequest;
import com.asanrezerv.restaurantservice.entity.Branch;
import com.asanrezerv.restaurantservice.entity.Restaurant;
import com.asanrezerv.restaurantservice.entity.RestaurantImage;
import com.asanrezerv.restaurantservice.entity.RestaurantTable;
import com.asanrezerv.restaurantservice.enums.PhotoType;
import com.asanrezerv.restaurantservice.enums.PublicationStatus;
import com.asanrezerv.restaurantservice.enums.TableStatus;
import com.asanrezerv.restaurantservice.exception.NotFoundException;
import com.asanrezerv.restaurantservice.exception.ReachedQuotaLimitException;
import com.asanrezerv.restaurantservice.mapper.BranchMapper;
import com.asanrezerv.restaurantservice.mapper.RestaurantMapper;
import com.asanrezerv.restaurantservice.mapper.TableMapper;
import com.asanrezerv.restaurantservice.repository.BranchRepository;
import com.asanrezerv.restaurantservice.repository.RestaurantImageRepository;
import com.asanrezerv.restaurantservice.repository.RestaurantRepository;
import com.asanrezerv.restaurantservice.repository.TableRepository;
import com.asanrezerv.restaurantservice.util.GoogleMapsLinkParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final BranchRepository branchRepository;
    private final TableRepository tableRepository;
    private final RestaurantMapper restaurantMapper;
    private final BranchMapper branchMapper;
    private final TableMapper tableMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final SubscriptionClient subscriptionClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MediaClient mediaClient;
    private final RestaurantImageRepository restaurantImageRepository;


    public RestaurantDto getRestaurantById(UUID id) {
        var opt = restaurantRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Restaurant not found");
        }

        return restaurantMapper.toRestaurantDto(opt.get());

    }

    public List<BranchDto> getBranchesByRestaurant(UUID id) {
        List<BranchDto> list = new ArrayList<>();

        var opt = restaurantRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Restaurant not found");
        }

        branchRepository.findBranchesByRestaurant(opt.get()).forEach(branch -> {
            list.add(branchMapper.toBranchDto(branch));
        });

        return list;
    }

    public List<RestaurantDto> getRestaurants(String search, String city, int page, int size) {
        String s = (search == null || search.isBlank()) ? null : "%" + search.trim().toLowerCase() + "%";
        String c = (city == null || city.isBlank()) ? null : city.trim().toLowerCase();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "visibilityRank"));
        return restaurantRepository.search(s, c, pageable).stream().map(restaurantMapper::toRestaurantDto).toList();
    }


    public TableDto changeTableStatus(UUID tableId, TableStatus status) {
        var opt = tableRepository.findById(tableId);
        if (opt.isEmpty()) {
            throw new NotFoundException("Masa tapılmadı!");
        }

        var table = opt.get();

        table.setStatus(status);

        table = tableRepository.save(table);

        return tableMapper.tableToDto(table);
    }


    // Management

    public RestaurantDto getUserRestaurant(UUID userId) {
        var opt = restaurantRepository.findRestaurantByOwnerId(userId);

        if (opt.isEmpty()) {
            throw new NotFoundException("Restoran tapılmadı!");
        }

        return restaurantMapper.toRestaurantDto(opt.get());
    }


    public RestaurantDto createMyRestaurant(UUID userId, RestaurantRequest request) {

        Restaurant restaurant = Restaurant.builder()
                .ownerId(userId)
                .name(request.getName())
                .cuisine(request.getCuisine())
                .city(request.getCity())
                .description(request.getDescription())
                .publicationStatus(PublicationStatus.DRAFT)
                .phone(request.getPhone())
                .mediaList(new ArrayList<>())
                .build();
        restaurant = restaurantRepository.save(restaurant);

        var restaurantDto = restaurantMapper.toRestaurantDto(restaurant);

        kafkaTemplate.send("restaurant.created", restaurantDto);

        return restaurantDto;


    }

    public RestaurantDto updateMyRestaurant(UUID userId, RestaurantRequest request) {

        var opt = restaurantRepository.findRestaurantByOwnerId(userId);

        if (opt.isEmpty()) {
            throw new NotFoundException("Restoran tapılmadı!");
        }
        Restaurant restaurant = opt.get();

        restaurant.setName(request.getName());
        restaurant.setCuisine(request.getCuisine());
        restaurant.setCity(request.getCity());
        restaurant.setDescription(request.getDescription());
        restaurant.setPhone(request.getPhone());

        restaurant = restaurantRepository.save(restaurant);

        var dto = restaurantMapper.toRestaurantDto(restaurant);

        kafkaTemplate.send("restaurant.updated", dto);
        return dto;

    }

    public void publishRestaurant(UUID id) {
        var opt =  restaurantRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Restaurant not found!");
        }
        var restaurant = opt.get();

        restaurant.setPublicationStatus(PublicationStatus.PUBLISHED);
        kafkaTemplate.send("restaurant.published", restaurant.getId());
        restaurantRepository.save(opt.get());
    }

    public List<BranchDto> getBranches(UUID userId) {
        List<BranchDto> branches = new ArrayList<>();

        var opt = restaurantRepository.findRestaurantByOwnerId(userId);

        if (opt.isEmpty()) {
            throw new NotFoundException("Restoran tapılmadı!");
        }
        var restaurant = opt.get();

        branchRepository.findBranchesByRestaurant(restaurant).forEach(branch -> {
            branches.add(branchMapper.toBranchDto(branch));
        });

        return branches;

    }

    public BranchDto getBranchById(UUID id) {
        var branch = branchRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Filial tapılmadı!"));
        return branchMapper.toBranchDto(branch);
    }

    public BranchDto createBranch(UUID userId, BranchRequest request) {


        var opt = restaurantRepository.findRestaurantByOwnerId(userId);

        if (opt.isEmpty()) {
            throw new NotFoundException("Restoran tapılmadı!");
        }

        Restaurant restaurant = opt.get();

        String json = stringRedisTemplate.opsForValue().get("restaurant:limit_" + restaurant.getId());

        QuotaLimits limits;

        if (Optional.ofNullable(json).isEmpty()) {
            limits = Optional.ofNullable(subscriptionClient.getLimits(restaurant.getId()).getData()).orElseThrow(() -> new NotFoundException("Plan not found!"));
            json = objectMapper.writeValueAsString(limits);
            stringRedisTemplate.opsForValue().set("restaurant:limit_" + restaurant.getId(), json);
        } else {
            limits = objectMapper.readValue(json, QuotaLimits.class);
        }

        json = stringRedisTemplate.opsForValue().get("restaurant_" + restaurant.getId());
        RestaurantQuotaUsage usage;

        if (Optional.ofNullable(json).isEmpty()) {
            usage = Optional.ofNullable(subscriptionClient.getRestaurantUsage(restaurant.getId()).getData()).orElseThrow(() -> new NotFoundException("Plan not found!"));
            json = objectMapper.writeValueAsString(usage);
            stringRedisTemplate.opsForValue().set("restaurant_" + restaurant.getId(), json, 1, TimeUnit.DAYS);
        } else {
            usage = objectMapper.readValue(json, RestaurantQuotaUsage.class);
        }



        if (limits.getMaxBranches() != -1 && usage.getCurrentBranchCount() >= limits.getMaxBranches()) {
            throw new ReachedQuotaLimitException("Siz yanlız " + limits.getMaxBranches() + " ədəd filial əlavə edə bilərsiniz!");
        }


        var coordinates = GoogleMapsLinkParser.parse(request.getGoogleMapsLink());

        Branch branch = Branch.builder()
                .restaurant(restaurant)
                .name(request.getName())
                .address(request.getAddress())
                .phone(request.getPhone())
                .city(Optional.ofNullable(request.getCity()).filter(c -> !c.isBlank()).orElse(restaurant.getCity()))
                .district(request.getDistrict())
                .googleMapsLink(request.getGoogleMapsLink())
                .latitude(coordinates != null ? coordinates.latitude() : null)
                .longitude(coordinates != null ? coordinates.longitude() : null)
                .openingTime(request.getOpeningTime())
                .closingTime(request.getClosingTime())
                .build();

        branch = branchRepository.save(branch);

        usage.setCurrentBranchCount(usage.getCurrentBranchCount() + 1);

        json = objectMapper.writeValueAsString(usage);

        stringRedisTemplate.opsForValue().set("restaurant_" + restaurant.getId(), json);

        kafkaTemplate.send("branch.created", buildBranchEvent(branch));

        return branchMapper.toBranchDto(branch);
    }

    // "branch.created" / "branch.updated" hadisələri üçün tam datanı qurur
    // (masa sayı dəyişəndə minTableCapacity/maxTableCapacity yenilənsin deyə createTable/deleteTable-dan da çağırılır).
    private BranchEventDto buildBranchEvent(Branch branch) {
        var capacities = tableRepository.findRestaurantTablesByBranch(branch).stream()
                .map(RestaurantTable::getCapacity)
                .toList();

        Integer min = capacities.stream().min(Integer::compareTo).orElse(0);
        Integer max = capacities.stream().max(Integer::compareTo).orElse(0);

        return BranchEventDto.builder()
                .restaurantId(branch.getRestaurant().getId())
                .branchId(branch.getId())
                .name(branch.getName())
                .city(branch.getCity())
                .district(branch.getDistrict())
                .address(branch.getAddress())
                .latitude(branch.getLatitude())
                .longitude(branch.getLongitude())
                .photosUrl(branch.getPhotosUrl())
                .minTableCapacity(min)
                .maxTableCapacity(max)
                .openingTime(branch.getOpeningTime() != null ? branch.getOpeningTime().toString() : null)
                .closingTime(branch.getClosingTime() != null ? branch.getClosingTime().toString() : null)
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }

    public BranchDto updateBranch(UUID userId, UUID id, BranchRequest request) {

        var opt = branchRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Filial tapılmadı!");
        }

        Branch branch = opt.get();
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setOpeningTime(request.getOpeningTime());
        branch.setClosingTime(request.getClosingTime());
        branch.setPhone(request.getPhone());
        if (request.getCity() != null && !request.getCity().isBlank()) branch.setCity(request.getCity());
        branch.setDistrict(request.getDistrict());
        branch.setGoogleMapsLink(request.getGoogleMapsLink());
        var coordinates = GoogleMapsLinkParser.parse(request.getGoogleMapsLink());
        branch.setLatitude(coordinates != null ? coordinates.latitude() : null);
        branch.setLongitude(coordinates != null ? coordinates.longitude() : null);
        branch = branchRepository.save(branch);

        kafkaTemplate.send("branch.updated", buildBranchEvent(branch));

        return branchMapper.toBranchDto(branch);


    }

    public void deleteBranch(UUID userId, UUID id) {
        var opt = branchRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Filial tapılmadı!");
        }

        var branch = opt.get();
        UUID restaurantId = branch.getRestaurant().getId();
        UUID branchId = branch.getId();
        branchRepository.deleteById(id);

        String json = stringRedisTemplate.opsForValue().get("restaurant_" + restaurantId);

        RestaurantQuotaUsage usage;

        if (Optional.ofNullable(json).isEmpty()) {
            usage = Optional.ofNullable(subscriptionClient.getRestaurantUsage(restaurantId).getData()).orElseThrow(() -> new NotFoundException("Restaurant: " + restaurantId + " not found!"));
            json = objectMapper.writeValueAsString(usage);
            stringRedisTemplate.opsForValue().set("restaurant_" + restaurantId, json);
        } else {
            usage = objectMapper.readValue(json, RestaurantQuotaUsage.class);
        }

        usage.setCurrentBranchCount(usage.getCurrentBranchCount() - 1);
        json = objectMapper.writeValueAsString(usage);
        stringRedisTemplate.opsForValue().set("restaurant_" + restaurantId, json);
        // DİQQƏT: bu topic-in mövcud consumer-i (subscription-service) restaurantId gözləyir — dəyişmə.
        kafkaTemplate.send("branch.deleted", restaurantId);
        // search-service ES sənədini silmək üçün ayrıca, yeni topic (branchId ilə).
        kafkaTemplate.send("branch.search.deleted", branchId);

    }

    public List<TableDto> getTables(UUID userId, UUID branchId) {


        List<TableDto> tables = new ArrayList<>();

        var opt = branchRepository.findById(branchId);

        if (opt.isEmpty()) {
            throw new NotFoundException("Filial tapılmadı!");
        }


        Branch branch = opt.get();

        tableRepository.findRestaurantTablesByBranch(branch).forEach(table -> {
            tables.add(tableMapper.tableToDto(table));
        });

        return tables;


    }


    public TableDto createTable(UUID userId, UUID branchId, TableRequest request) {

        var opt = branchRepository.findById(branchId);
        if (opt.isEmpty()) {
            throw new NotFoundException("Filial tapılmadı!");
        }

        Branch branch = opt.get();

        UUID restaurantId = branch.getRestaurant().getId();

        String json = stringRedisTemplate.opsForValue().get("restaurant:limit_" + restaurantId);
        QuotaLimits limits;
        if (Optional.ofNullable(json).isEmpty()) {
            limits = Optional.ofNullable(subscriptionClient.getLimits(restaurantId).getData()).orElseThrow(() -> new NotFoundException("Plan not found!"));
            json = objectMapper.writeValueAsString(limits);
            stringRedisTemplate.opsForValue().set("restaurant:limit_" + restaurantId, json);
        } else {
            limits = objectMapper.readValue(json, QuotaLimits.class);
        }


        json = stringRedisTemplate.opsForValue().get("restaurant:branch_" + branch.getId());
        BranchQuotaUsage usage;
        if (Optional.ofNullable(json).isEmpty()) {
            usage = Optional.ofNullable(subscriptionClient.getBranchUsage(branch.getId()).getData()).orElseThrow(() -> new NotFoundException("Plan not found!"));
            json = objectMapper.writeValueAsString(usage);
            stringRedisTemplate.opsForValue().set("restaurant:branch_" + branch.getId(), json, 1, TimeUnit.DAYS);
        } else {
            usage = objectMapper.readValue(json, BranchQuotaUsage.class);
        }


        if (limits.getMaxTablesPerBranch() != -1 && usage.getCurrentTableCount() >= limits.getMaxTablesPerBranch()) {
            throw new ReachedQuotaLimitException("Siz yanlız " + limits.getMaxTablesPerBranch() + " ədəd masa əlavə edə bilərsiniz!");
        }

        RestaurantTable table = RestaurantTable.builder().branch(branch).name(request.getName()).capacity(request.getCapacity()).zone(request.getZone()).type(request.getType()).build();

        table = tableRepository.save(table);


        usage.setCurrentTableCount(usage.getCurrentTableCount() + 1);
        json = objectMapper.writeValueAsString(usage);
        stringRedisTemplate.opsForValue().set("restaurant:branch_" + branch.getId(), json);

        var event = TableCreationEvent.builder().restaurantId(branch.getRestaurant().getId()).branchId(branch.getId()).build();

        kafkaTemplate.send("table.created", event);
        // Masa sayı/tutumu dəyişdi — search index-də min/max tutumu təzələ
        kafkaTemplate.send("branch.updated", buildBranchEvent(branch));

        return tableMapper.tableToDto(table);


    }


    public TableDto updateTable(UUID userId, UUID tableId, TableRequest request) {

        var opt = tableRepository.findById(tableId);

        if (opt.isEmpty()) {
            throw new NotFoundException("Masa tapılmadı!");
        }

        RestaurantTable table = opt.get();

        table.setName(request.getName());
        table.setCapacity(request.getCapacity());
        table.setZone(request.getZone());
        table.setType(request.getType());

        table = tableRepository.save(table);

        // Tutum dəyişmiş ola bilər — search index-də min/max tutumu təzələ
        kafkaTemplate.send("branch.updated", buildBranchEvent(table.getBranch()));

        return tableMapper.tableToDto(table);
    }

    public void deleteTable(UUID userId, UUID tableId) {

        var opt = tableRepository.findById(tableId);
        if (opt.isEmpty()) {
            throw new NotFoundException("Filial tapılmadı!");
        }


        Branch branch = opt.get().getBranch();
        UUID branchId = branch.getId();


        String json = stringRedisTemplate.opsForValue().get("restaurant:branch_" + branchId);

        BranchQuotaUsage usage;

        if (Optional.ofNullable(json).isEmpty()) {
            usage = Optional.ofNullable(subscriptionClient.getBranchUsage(branchId).getData()).orElseThrow(() -> new NotFoundException("Branch: " + branchId + " not found!"));
            json = objectMapper.writeValueAsString(usage);
            stringRedisTemplate.opsForValue().set("restaurant:branch_" + branchId, json);
        } else {
            usage = objectMapper.readValue(json, BranchQuotaUsage.class);
        }


        usage.setCurrentTableCount(usage.getCurrentTableCount() - 1);
        json = objectMapper.writeValueAsString(usage);
        stringRedisTemplate.opsForValue().set("restaurant:branch_" + branchId, json);
        kafkaTemplate.send("table.deleted", branchId);

        tableRepository.deleteById(tableId);

        // Masa artıq silinib — indi yenidən hesabla ki, min/max tutuma daxil olmasın
        kafkaTemplate.send("branch.updated", buildBranchEvent(branch));
    }


    public String uploadImage(UUID userId, PhotoType type, MultipartFile file) {
        var res = restaurantRepository.findRestaurantByOwnerId(userId).orElseThrow(() -> new NotFoundException("Restaurant not found!"));

        Map<String,String> imgUrl = mediaClient.upload(file).getData();
        if (imgUrl == null || imgUrl.get("url") == null) {
            throw new IllegalArgumentException("Şəkil yüklənmədi — fayl etibarlı şəkil deyil və ya yükləmə uğursuz oldu.");
        }


        RestaurantImage img = RestaurantImage.builder()
                .imageUrl(imgUrl.get("url"))
                .publicId(imgUrl.get("publicId"))
                .mediaType(type)
                .restaurant(res)
                .build();

        var list = res.getMediaList();
        list.add(img);
        res.setMediaList(list);

        restaurantRepository.save(res);
        img = restaurantImageRepository.save(img);

        return img.getImageUrl();
    }

    public String uploadBranchImage(UUID userId, UUID branchId, MultipartFile file) {
        var branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Filial tapılmadı!"));

        if (!branch.getRestaurant().getOwnerId().equals(userId)) {
            throw new NotFoundException("Filial tapılmadı!");
        }

        Map<String, String> imgUrl = mediaClient.upload(file).getData();
        if (imgUrl == null || imgUrl.get("url") == null) {
            throw new IllegalArgumentException("Şəkil yüklənmədi — fayl etibarlı şəkil deyil və ya yükləmə uğursuz oldu.");
        }

        branch.getPhotosUrl().add(imgUrl.get("url"));
        branch = branchRepository.save(branch);

        kafkaTemplate.send("branch.updated", buildBranchEvent(branch));

        return imgUrl.get("url");
    }


}
