package com.grepp.datenow.app.model.place.service;

import com.grepp.datenow.app.model.course.dto.CourseDetailDto;
import com.grepp.datenow.app.model.course.dto.CourseDto;
import com.grepp.datenow.app.model.course.dto.EditorCourseDto;
import com.grepp.datenow.app.model.course.dto.EditorDetailCourseDto;
import com.grepp.datenow.app.model.course.entity.Course;
import com.grepp.datenow.app.model.course.entity.CourseHashtag;
import com.grepp.datenow.app.model.course.entity.EditorCourse;
import com.grepp.datenow.app.model.course.entity.Hashtag;
import com.grepp.datenow.app.model.course.entity.RecommendCourse;
import com.grepp.datenow.app.model.course.repository.AdminCourseRepository;
import com.grepp.datenow.app.model.course.repository.CourseRepository;
import com.grepp.datenow.app.model.course.repository.RecommendCourseRepository;
import com.grepp.datenow.app.model.image.entity.Image;
import com.grepp.datenow.app.model.image.repository.ImageRepository;
import com.grepp.datenow.app.model.like.repository.FavoriteRepository;
import com.grepp.datenow.app.model.course.dto.CourseCountDto;
import com.grepp.datenow.app.model.place.dto.PlaceDetailDto;
import com.grepp.datenow.app.model.place.dto.mainpage.AdminUserTopListDto;
import com.grepp.datenow.app.model.place.entity.Place;
import com.grepp.datenow.app.model.place.repository.PlaceRepository;
import com.grepp.datenow.app.model.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceMainPageService {

    private final AdminCourseRepository adminCourseRepository;
    private final CourseRepository courseRepository;
    private final ImageRepository imageRepository;
    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;
    private final RecommendCourseRepository recommendCourseRepository;

    @Transactional(readOnly = true)
    public AdminUserTopListDto mainPagelist() {
        Pageable pageable =  PageRequest.of(0, 4);

        // 메인 목록 조회
        List<EditorCourse> adminplace = adminCourseRepository.findTop4ByOrderByCreatedAtDesc(pageable);
        List<RecommendCourse> userplace = courseRepository.findTop4ByOrderByCreatedAtDesc(pageable);

        // 이미지 일괄 조회
        Map<Long, String> editorImageMap = adminplace.isEmpty() ? Map.of() :
            imageRepository.findAllByEditorCourseIdIn(adminplace).stream()
                .collect(Collectors.toMap(
                    i -> i.getEditorCourseId().getEditorCourseId(),
                    Image::getSavePath,
                    (first, second) -> first));

        Map<Long, String> recommendImageMap = userplace.isEmpty() ? Map.of() :
            imageRepository.findAllByRecommendCourseIdIn(userplace).stream()
                .collect(Collectors.toMap(
                    i -> i.getRecommendCourseId().getRecommendCourseId(),
                    Image::getSavePath,
                    (first, second) -> first));

        // 좋아요 수 일괄 조회
        Map<Long, Long> editorLikeMap = adminplace.isEmpty() ? Map.of() :
            favoriteRepository.countByEditorCoursesGrouped(adminplace).stream()
                .collect(Collectors.toMap(CourseCountDto::courseId, CourseCountDto::count));

        Map<Long, Long> recommendLikeMap = userplace.isEmpty() ? Map.of() :
            favoriteRepository.countByRecommendCoursesGrouped(userplace).stream()
                .collect(Collectors.toMap(CourseCountDto::courseId, CourseCountDto::count));

        // 리뷰 수 일괄 조회
        Map<Long, Long> reviewCountMap = userplace.isEmpty() ? Map.of() :
            reviewRepository.countByRecommendCoursesGrouped(userplace).stream()
                .collect(Collectors.toMap(CourseCountDto::courseId, CourseCountDto::count));

        // DTO 변환
        List<EditorCourseDto> adminDto = adminplace.stream()
            .map(course -> {
                String imageUrl = editorImageMap.getOrDefault(
                    course.getEditorCourseId(), "/images/image-fallback.jpg");
                int likeCount = editorLikeMap.getOrDefault(
                    course.getEditorCourseId(), 0L).intValue();
                return new EditorCourseDto(course, imageUrl, likeCount);
            })
            .toList();

        List<CourseDto> userDto = userplace.stream()
            .map(course -> {
                String imageUrl = recommendImageMap.getOrDefault(
                    course.getRecommendCourseId(), "/images/image-fallback.jpg");
                int likeCount = recommendLikeMap.getOrDefault(
                    course.getRecommendCourseId(), 0L).intValue();
                int reviewCount = reviewCountMap.getOrDefault(
                    course.getRecommendCourseId(), 0L).intValue();
                return new CourseDto(course, imageUrl, likeCount, reviewCount);
            })
            .toList();

        return new AdminUserTopListDto(adminDto,userDto);
    }

    @Transactional
    public List<EditorCourseDto> adminPageList() {
        List<EditorCourse> adminPlace = adminCourseRepository.findAllByActivatedTrue();
        List<EditorCourseDto> adminDto = adminPlace.stream()
            .map(course -> {
              Image img = imageRepository.findFirstByEditorCourseIdAndActivatedTrue(course).orElse(null);
              int likeCount = favoriteRepository.countByEditorCourseAndActivatedTrue(course);
              String imageUrl = (img != null)
                  ? img.getSavePath() // s3 url
                  : "/images/image-fallback.jpg";
              return new EditorCourseDto(course, imageUrl, likeCount);
            })
            .toList();

        return adminDto;
    }

    @Transactional
    public List<CourseDto> recommendCourseService(@Nullable List<String> hashtagNames) {
        List<RecommendCourse> userPlace;
        if (hashtagNames != null && !hashtagNames.isEmpty()) {
          // ⭐⭐ [추가] 해시태그로 필터링하여 조회 ⭐⭐
          // 이 메서드는 아래 레포지토리에서 새로 정의할 것입니다.
          userPlace = courseRepository.findAllCourseWithHashtags(hashtagNames);
        } else {
          // ⭐⭐ [수정] 해시태그가 없으면 기존처럼 전체 조회 ⭐⭐
          userPlace = courseRepository.findAllWithCourseAndMember();
        }
        List<CourseDto> courseDto = userPlace.stream()
          .map(recommendCourse -> {
              // Course 엔티티를 별도의 변수에 할당하여 사용하면 코드 가독성이 좋아집니다.
              Course course = recommendCourse.getCourseId(); // RecommendCourse에서 Course 엔티티를 가져옴

              Image img = imageRepository.findFirstByRecommendCourseIdAndActivatedTrue(recommendCourse)
                  .orElse(null);
              int count = favoriteRepository.countByRecommendCourseAndActivatedTrue(recommendCourse);
              int reviewCnt = reviewRepository.countByRecommendCourseIdAndActivatedTrue(recommendCourse);
              String imageUrl = (img != null)
                  ? img.getSavePath() // s3 url
                  : "/images/image-fallback.jpg";

              List<String> currentCourseHashtags = course.getCourseHashtags().stream() // ⭐⭐⭐ 여기서 course.getCourseHashtags()로 수정 ⭐⭐⭐
                  .map(CourseHashtag::getHashtag)
                  .map(Hashtag::getTagName)
                  .collect(Collectors.toList());

              CourseDto dto = new CourseDto(); // ⭐ 기본 생성자 호출
              dto.setCourseId(recommendCourse.getRecommendCourseId());
              dto.setTitle(course.getTitle());
              dto.setCreatorNickname(course.getId().getNickname()); // ⭐ Course의 Member 필드가 'member'인 경우
              dto.setDescription(course.getDescription());
              dto.setImageUrl(imageUrl);
              dto.setFavoriteCnt(count);
              dto.setReviewCnt(reviewCnt);
              dto.setHashtagNames(currentCourseHashtags);

              return dto;

          })
          .toList();
        return courseDto;
    }


    @Transactional
    public CourseDetailDto userDetailPlace(Long recommendId) {
        RecommendCourse recommendCourse = courseRepository.findWithCourseAndMemberById(recommendId)
            .orElseThrow(() -> new EntityNotFoundException("엔티티가 존재하지 않습니다."));
        CourseDetailDto placeDetail = new CourseDetailDto();
        placeDetail.setTitle(recommendCourse.getCourseId().getTitle());
        placeDetail.setNickname(recommendCourse.getCourseId().getId().getNickname());
        placeDetail.setCreatedAt(recommendCourse.getCreatedAt());
        placeDetail.setDescription(recommendCourse.getCourseId().getDescription());
        Course course = recommendCourse.getCourseId();
        List<Place> places = placeRepository.findAllByCourseId(course);
        List<PlaceDetailDto> placeDetailDtos = places.stream()
                .map(PlaceDetailDto::new)
                    .toList();

        placeDetail.setPlaces(placeDetailDtos);
        List<Image> image = imageRepository.findAllByRecommendCourseIdAndActivatedTrue(recommendCourse);
        List<String> imageUrl = image.stream()
            .map(img -> {
              if(img != null){
                return img.getSavePath(); // s3 url
              }
              else {
                return  "/images/image-fallback.jpg";
              }
            })
                .toList();

        placeDetail.setImageUrl(imageUrl);
          // ⭐⭐⭐ 해시태그 정보 매핑 (새로 추가) ⭐⭐⭐
          // Course 엔티티에서 CourseHashtag 컬렉션 가져오기
          List<String> hashtagNames = course.getCourseHashtags().stream()
              // CourseHashtag 객체에서 실제 Hashtag 엔티티를 가져옴
              .map(CourseHashtag::getHashtag)
              // Hashtag 엔티티에서 tagName을 가져옴
              .map(Hashtag::getTagName)
              // Stream의 결과를 List<String>으로 수집
              .collect(Collectors.toList());
          placeDetail.setHashtagNames(hashtagNames); // CourseDetailDto에 해시태그 목록 설정
          // ⭐⭐⭐ 끝 ⭐⭐⭐

        return placeDetail;
    }

    @Transactional
    public EditorDetailCourseDto editorDetailPlace(Long editorId) {
        EditorCourse editorCourse = adminCourseRepository.findWithMemberById(editorId)
            .orElseThrow(() -> new EntityNotFoundException("엔티티가 존재하지 않습니다."));
        EditorDetailCourseDto placeDetail = new EditorDetailCourseDto();
        placeDetail.setTitle(editorCourse.getTitle());
        placeDetail.setNickname(editorCourse.getMember().getNickname());
        placeDetail.setCreatedAt(editorCourse.getCreatedAt());
        placeDetail.setDescription(editorCourse.getDescription());

        List<Place> places = placeRepository.findAllByEditorCourseId(editorCourse);
        List<PlaceDetailDto> placeDetailDtos = places.stream()
            .map(PlaceDetailDto::new)
            .toList();

        placeDetail.setPlaces(placeDetailDtos);
        List<Image> image = imageRepository.findAllByEditorCourseIdAndActivatedTrue(editorCourse);
        List<String> imageUrl = image.stream()
            .map(img -> {
              if(img != null){
                return img.getSavePath(); // s3 url;
              }
              else {
                return  "/images/image-fallback.jpg";
              }
            })
            .toList();

        placeDetail.setImageUrl(imageUrl);
        return placeDetail;
    }

    @Transactional
    public void deactivateRecommendCourse(Course course){
        recommendCourseRepository.findByCourseIdAndActivatedTrue(course)
            .ifPresent(recommendCourse -> {
                recommendCourse.unActivated();
                favoriteRepository.deactiveAllByRecommendCourse(recommendCourse);
            });

    }
}
