package subway.line;

import java.util.List;
import subway.section.Section;
import subway.station.Station;

public class LineResponse {

  private Long id;
  private String name;
  private String color;
  private Station upStation;
  private Station downStation;
  private List<Section> sections;
  private Long distance;

  public LineResponse(Long id, String name, String color, Station upStation, Station downStation, List<Section> sections, Long distance) {
    this.id = id;
    this.name = name;
    this.color = color;
    this.upStation = upStation;
    this.downStation = downStation;
    this.sections = sections;
    this.distance = distance;
  }

  public static LineResponse of(Line line) {
    return new LineResponse(
        line.getId(),
        line.getName(),
        line.getColor(),
        line.getUpStation(),
        line.getDownStation(),
        line.getSections(),
        line.getDistance()
    );
  }

  public Line toEntity() {
    return new Line(id, name, color, upStation, downStation, sections, distance);
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getColor() {
    return color;
  }

  public Station getUpStation() {
    return upStation;
  }

  public Station getDownStation() {
    return downStation;
  }

  public Long getDistance() {
    return distance;
  }
}
