package ar.net.ut.backend.user.mapper;

import ar.net.ut.backend.Global;
import ar.net.ut.backend.career.CareerMapper;
import ar.net.ut.backend.course.mapper.CourseMapper;
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
        uses = {CareerMapper.class, CourseMapper.class}
)
public abstract class UserMapper {

    @Mapping(source = "referredBy.id", target = "referredById")
    @Mapping(source = "bookmarkedCourses", target = "bookmarkedCourses", qualifiedByName = "bookmarkedCoursesToCourseDTOs")
    public abstract UserDTO toDTO(User user);

    @Mapping(source = "pictureKey", target = "pictureURL", qualifiedByName = "pictureKeyToURL")
    public abstract UserProfileDTO toProfileDTO(UserProfile profile);

    @Mapping(target = "bookmarkedCourses", ignore = true)
    public abstract void updateFromDTO(@MappingTarget User user, UserUpdateDTO dto);

    @Mapping(source = "careerId", target = "career", qualifiedByName = "careerIdToCareer")
    public abstract void updateProfileFromDTO(@MappingTarget UserProfile profile, UserProfileUpdateDTO dto);

    @Named("pictureKeyToURL")
    public String mapPictureKeyToPictureURL(String pictureKey) {
        if (pictureKey == null) return null;
        return Global.R2.PUBLIC_URL + "/" + pictureKey;
    }
}
