package site.gunwoo.forecastBE.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

//단기예보 조회 api에 대한 응답을 받는 객체
@Getter @Setter
public class ShortForeCastResponseDTO {

    private Response response;

    @Getter
    @Setter
    public static class Response {
        private Header header;
        private Body body;

        @Getter
        @Setter
        public static class Header {
            private String resultCode;
            private String resultMsg;
        }

        @Getter
        @Setter
        public static class Body {
            private String dataType;
            private Items items;
            private int pageNo;
            private int numOfRows;
            private int totalCount;

            @Getter
            @Setter
            public static class Items {
                private List<ForecastItem> item;

                @Getter
                @Setter
                public static class ForecastItem {
                    private String baseDate;
                    private String baseTime;
                    private String category;
                    private String fcstDate;
                    private String fcstTime;
                    private String fcstValue;
                    private int nx;
                    private int ny;
                }
            }
        }
    }
}
