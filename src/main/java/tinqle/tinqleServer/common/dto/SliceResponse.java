package tinqle.tinqleServer.common.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;

import java.util.List;

@Builder
@Getter
@Slf4j
public class SliceResponse<T> {

    boolean isLast;
    List<T> content;

    public static <T> SliceResponse<T> of(Slice<T> slice) {
        return SliceResponse.<T>builder()
                .isLast(slice.isLast())
                .content(slice.getContent())
                .build();
    }
}
