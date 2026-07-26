// Azərbaycan mobil nömrə formatlayıcısı.
// İstifadəçi necə yazır-yazsın (0506379198, 506379198, +994506379198, 994 50 637 91 98...)
// backend-ə həmişə "+994 (50) 637 91 98" formatında göndəririk.

// İnput zamanı (yazdıqca) formatlaşdırır — inputun value-suna birbaşa verilir.
export function formatAzPhoneInput(raw) {
  let digits = (raw || '').replace(/\D/g, '');

  // Aparıcı ölkə kodunu (994) at ki, aşağıdakı hissələr həmişə operator+nömrə olsun
  if (digits.startsWith('994')) digits = digits.slice(3);
  // Yerli formatdakı aparıcı "0"-ı at (0506379198 -> 506379198)
  else if (digits.startsWith('0')) digits = digits.slice(1);

  digits = digits.slice(0, 9); // operator kodu (2) + nömrə (7) = 9 rəqəm

  const operator = digits.slice(0, 2);
  const part1 = digits.slice(2, 5);
  const part2 = digits.slice(5, 7);
  const part3 = digits.slice(7, 9);

  let result = '+994';
  if (operator) result += ` (${operator}`;
  if (digits.length >= 2) result += ')';
  if (part1) result += ` ${part1}`;
  if (part2) result += ` ${part2}`;
  if (part3) result += ` ${part3}`;
  return result;
}

// Backend-ə göndərməzdən əvvəl doğrulama: tam 9 rəqəm (operator+nömrə) yığılıbmı?
export function isCompleteAzPhone(formatted) {
  const digits = (formatted || '').replace(/\D/g, '').replace(/^994/, '');
  return digits.length === 9;
}
