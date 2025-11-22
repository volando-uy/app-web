package mappers;

import adapters.LocalDateWithValue;
import com.labpa.appweb.user.BaseCustomerDTO;

public class CustomerMapper {

    public static BaseCustomerDTO toSoapCustomer(BaseCustomerDTO appDto) {
        BaseCustomerDTO soapDto = new BaseCustomerDTO();

        soapDto.setNickname(appDto.getNickname());
        soapDto.setName(appDto.getName());
        soapDto.setMail(appDto.getMail());
        soapDto.setPassword(appDto.getPassword());

        soapDto.setSurname(appDto.getSurname());
        soapDto.setCitizenship(appDto.getCitizenship());

        // Enum mapping
        if (appDto.getDocType() != null) {
            soapDto.setDocType(com.labpa.appweb.user.EnumTipoDocumento.valueOf(appDto.getDocType().name()));
        }

        soapDto.setNumDoc(appDto.getNumDoc());

        if (appDto.getBirthDate() != null) {
            soapDto.setBirthDate(new LocalDateWithValue(appDto.getBirthDate().toString()));
        }

        return soapDto;
    }

}
