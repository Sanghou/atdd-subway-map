package subway;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import subway.Mocks.MockLine;
import subway.Mocks.MockStation;
import subway.line.Line;
import subway.line.LineRequest;
import subway.station.Station;

@DisplayName("지하철 노선 관리 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class LineAcceptanceTest {

  /**
   * When 지하철 노선을 생성하면
   * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
   */
  @DisplayName("지하철노선 생성 테스트")
  @Test
  void 지하철_노선_생성_테스트() {
    // when
    Line line = 지하철_노선_생성(MockLine.서울2호선, MockStation.서울대입구역, MockStation.봉천역);

    assertThat(
        지하철_노선_조회(line.getId()).jsonPath().getString("name")
    ).isEqualTo(line.getName());
  }

  /**
   * Given 2개의 지하철 노선을 생성하고
   * When 지하철 노선 목록을 조회하면
   * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
   */
  @DisplayName("지하철노선 목록 조회 테스트")
  @Test
  void 지하철_노선_목록_조회_테스트() {
    Line line1 = 지하철_노선_생성(MockLine.서울2호선, MockStation.서울대입구역, MockStation.봉천역);
    Line line2 = 지하철_노선_생성(MockLine.신분당선, MockStation.강남역, MockStation.신사역);

    assertThat(
        지하철_노선_목록_조회().jsonPath().getList("name", String.class)
    ).containsAll(List.of(line1.getName(),line2.getName()));
  }

  /**
   * Given 지하철 노선을 생성하고
   * When 생성한 지하철 노선을 조회하면
   * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
   */
  @DisplayName("지하철노선 조회 테스트")
  @Test
  void 지하철_노선_조회_테스트() {
    // given
    Line created = 지하철_노선_생성(MockLine.서울2호선, MockStation.서울대입구역, MockStation.봉천역);

    // when
    ExtractableResponse<Response> show = 지하철_노선_조회(created.getId());

    // then
    assertThat(show.jsonPath().getString("name")).isEqualTo(created.getName());
  }

  /**
   * Given 지하철 노선을 생성하고
   * When 생성한 지하철 노선을 수정하면
   * Then 해당 지하철 노선 정보는 수정된다
   */
  @DisplayName("지하철노선 수정 테스트")
  @Test
  void 지하철_노선_수정_테스트() {
    // given
    Line created = 지하철_노선_생성(MockLine.서울2호선, MockStation.서울대입구역, MockStation.봉천역);

    // when
    Line updated = 지하철_노선_수정(created.getId(), "이름이_바뀐_2호선", MockStation.서울대입구역, MockStation.봉천역);

    // then
    Line show = 지하철_노선_조회(updated.getId()).as(Line.class);
    assertAll(
        () -> assertThat(show.getName()).isEqualTo(updated.getName()),
        () -> assertThat(show.getInboundStation()).isEqualTo(updated.getInboundStation()),
        () -> assertThat(show.getOutboundStation()).isEqualTo(updated.getOutboundStation())
    );
  }

  /**
   * Given 지하철 노선을 생성하고
   * When 생성한 지하철 노선을 삭제하면
   * Then 해당 지하철 노선 정보는 삭제된다.
   */
  @DisplayName("지하철노선 삭제 테스트")
  @Test
  void 지하철_노선_삭제_테스트() {
    // given
    Line line = 지하철_노선_생성(MockLine.서울2호선, MockStation.서울대입구역, MockStation.봉천역);

    // when
    지하철_노선_삭제(line.getId());

    // then
    assertThat(지하철_노선_조회(line.getId()).statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
  }

  private Line 지하철_노선_생성(String name, Station inbound, Station outbound) {
    LineRequest request = new LineRequest(name, inbound, outbound);

    return RestAssured
        .given().body(request).contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().post("/lines")
        .then()
        .extract().body().as(Line.class);
  }

  private ExtractableResponse<Response> 지하철_노선_조회(Long id) {
    return RestAssured
        .given().contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().get("/lines/" + id)
        .then()
        .extract();
  }

  private ExtractableResponse<Response> 지하철_노선_목록_조회() {
    return RestAssured
        .given().contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().get("/lines")
        .then()
        .extract();
  }

  private Line 지하철_노선_수정(Long id, String name, Station inbound, Station outbound) {
    LineRequest request = new LineRequest(name, inbound, outbound);

    return RestAssured
        .given().body(request).contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().patch("/lines/" + id)
        .then()
        .extract().body().as(Line.class);
  }

  private void 지하철_노선_삭제(Long id) {
    RestAssured
        .given().log().all().contentType(MediaType.APPLICATION_JSON_VALUE)
        .when().delete("/lines/" + id);
  }
}