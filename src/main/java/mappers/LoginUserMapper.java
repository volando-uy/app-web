//package mappers;
//
//import com.labpa.appweb.auth.SoapLoginResponseDTO;
//import com.labpa.appweb.auth.SoapUserDTO;
//import domain.dtos.user.LoginResponseDTO;
//import domain.dtos.user.UserDTO;
//
//public class LoginUserMapper {
//    public static LoginResponseDTO fromSoap(SoapLoginResponseDTO soapDto) {
//        if (soapDto == null) return null;
//
//        return new LoginResponseDTO(
//                soapDto.getToken(),
//                fromSoap(soapDto.getUser())
//        );
//    }
//
//    public static UserDTO fromSoap(SoapUserDTO soapUser) {
//        if (soapUser == null) return null;
//
//        UserDTO dto = new UserDTO() {}; // Clase anónima concreta
//        dto.setNickname(soapUser.getNickname());
//        dto.setName(soapUser.getName());
//        dto.setMail(soapUser.getMail());
//        dto.setImage(soapUser.getImage());
//        dto.setPassword(null); // Nunca se debería exponer la contraseña
//
//        return dto;
//    }
//}
