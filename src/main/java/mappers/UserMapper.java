package mappers;

import com.labpa.appweb.auth.SoapUserDTO;
import com.labpa.appweb.user.BaseAirlineDTO;
import com.labpa.appweb.user.BaseCustomerDTO;
import com.labpa.appweb.user.UserDTO;

public class UserMapper {

    public static UserDTO fromSoap(SoapUserDTO soapUser, boolean isCustomer) {
        if (soapUser == null) return null;

        if (isCustomer) {
            BaseCustomerDTO customer = new BaseCustomerDTO();
            customer.setNickname(soapUser.getNickname());
            customer.setName(soapUser.getName());
            customer.setMail(soapUser.getMail());
            customer.setImage(soapUser.getImage());
            customer.setPassword(null); // Nunca exponer la contraseña
            return customer;
        } else {
            BaseAirlineDTO airline = new BaseAirlineDTO();
            airline.setNickname(soapUser.getNickname());
            airline.setName(soapUser.getName());
            airline.setMail(soapUser.getMail());
            airline.setImage(soapUser.getImage());
            airline.setPassword(null);
            return airline;
        }
    }
}
