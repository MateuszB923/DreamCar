package pl.dreamcar.mateuszbochenek.mappers;

import org.springframework.stereotype.Component;
import pl.dreamcar.mateuszbochenek.dto.CarReviewResponse;
import pl.dreamcar.mateuszbochenek.model.CarReview;

@Component
public class ReviewMapper {

    public CarReviewResponse toReviewResponse(CarReview review) {
        return CarReviewResponse.builder()
                .id(review.getId())
                .author(review.getAuthor())
                .review(review.getReview())
                .build();
    }
}
