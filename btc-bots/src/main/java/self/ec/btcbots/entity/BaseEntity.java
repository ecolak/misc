package self.ec.btcbots.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "date_created")
  private Long dateCreated;

  @Column(name = "date_modified")
  private Long dateModified;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Long dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Long getDateModified() {
    return dateModified;
  }

  public void setDateModified(Long dateModified) {
    this.dateModified = dateModified;
  }

}
