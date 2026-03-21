package ar.net.ut.backend.user.mapper;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.career.CareerMapper;
import ar.net.ut.backend.user.dto.UserDTO;
import ar.net.ut.backend.user.dto.UserUpdateDTO;
import ar.net.ut.backend.user.dto.profile.UserProfileDTO;
import ar.net.ut.backend.user.dto.profile.UserProfileUpdateDTO;
import ar.net.ut.backend.user.User;
import ar.net.ut.backend.user.UserProfile;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CareerMapper.class}
)
public abstract class UserMapper {

    @Mapping(source = "user.referredBy.id", target = "referredById")
    public abstract UserDTO toDTO(User user);

    @Mapping(source = "profile.pictureKey", target = "pictureURL", qualifiedByName = "pictureKeyToURL")
    public abstract UserProfileDTO toProfileDTO(UserProfile profile);

    public abstract void updateFromDTO(@MappingTarget User user, UserUpdateDTO dto);

    @Mapping(source = "careerId", target = "career", qualifiedByName = "careerIdToCareer")
    public abstract void updateProfileFromDTO(@MappingTarget UserProfile profile, UserProfileUpdateDTO dto);

    @Named("pictureKeyToURL")
    public String mapPictureKeyToPictureURL(String pictureKey) {
        if (pictureKey == null) return null;
        return Global.R2.PUBLIC_URL + "/" + pictureKey;
    }
}
