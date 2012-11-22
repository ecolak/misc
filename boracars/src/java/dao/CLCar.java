/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import task.CLCarCrawler;
import util.HibernateUtil;

/**
 *
 * @author ecolak
 */
@Entity
@Table(name = "cl_cars")
public class CLCar implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String HQL_GET_BY_ID = "SELECT c FROM CLCar c WHERE "
            + "c.carId = :carId";

    private static final String HQL_GET_BY_CL_ID = "SELECT c FROM CLCar c WHERE "
            + "c.craigslistId = :craigslistId";

    private static final String HQL_GET_ALL = "SELECT c FROM CLCar c";

    private static final String HQL_GET_MAX_LISTING_DATE = "SELECT max(listingDate) FROM CLCar c";

    public static NumberFormat gf;
    static {
        gf = DecimalFormat.getInstance();
        gf.setGroupingUsed(true);
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    @Column(name = "year")
    private Integer year;

    @Column(name = "mileage")
    private Integer mileage;

    @Column(name = "location")
    private String location;

    @Column(name = "cl_region")
    private String clRegion;

    @Column(name = "state")
    private String state;

    @Column(name = "price")
    private Integer price;

    @Column(name = "listing_title")
    private String listingTitle;

    @Column(name = "listing_url")
    private String listingUrl;

    @Column(name = "listing_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date listingDate;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "sold_by")
    private String soldBy;

    @Column(name = "craigslist_id")
    private Long craigslistId;

    @Basic(optional = false)
    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Column(name = "date_modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateModified;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "flagged")
    private boolean flagged;

    public CLCar() {
    }

    public CLCar(Integer carId) {
        this.carId = carId;
    }

    public CLCar(Integer carId, Date dateCreated) {
        this.carId = carId;
        this.dateCreated = dateCreated;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getClRegion() {
        return clRegion;
    }

    public void setClRegion(String clRegion) {
        this.clRegion = clRegion;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getListingTitle() {
        return listingTitle;
    }

    public void setListingTitle(String listingTitle) {
        this.listingTitle = listingTitle;
    }

    public String getListingUrl() {
        return listingUrl;
    }

    public void setListingUrl(String listingUrl) {
        this.listingUrl = listingUrl;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getSoldBy() {
        return soldBy;
    }

    public void setSoldBy(String soldBy) {
        this.soldBy = soldBy;
    }

    public Long getCraigslistId() {
        return craigslistId;
    }

    public void setCraigslistId(Long craigslistId) {
        this.craigslistId = craigslistId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public String getFormattedPrice() {
        String result = "";
        if(price != null) {
            result = "$" + gf.format(price);
        }
        return result;
    }

    public String getFormattedMileage() {
        String result = "";
        if(mileage != null) {
            result = gf.format(mileage);
        }
        return result;
    }

    public String getListingTimestamp() {
        String result = "";
        try {
            result = CLCarCrawler.POSTING_DATE_FORMAT.format(this.listingDate);
        } catch(Exception e) { }
        return result;
    }

    public static List<CLCar> getAll() {
        List<CLCar> result = new ArrayList<CLCar>();
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_ALL);
        result = query.list();
        tx.commit();

        return result;
    }

    public static CLCar getById(Integer carId) throws HibernateException {
        CLCar result = null;
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_BY_ID);
        query.setInteger("carId", carId);
        result = (CLCar)query.uniqueResult();
        tx.commit();

        return result;
    }

    public static CLCar getByCLId(Long craigslistId) throws HibernateException {
        CLCar result = null;
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_BY_CL_ID);
        query.setLong("craigslistId", craigslistId);
        result = (CLCar)query.uniqueResult();
        tx.commit();

        return result;
    }

    public static List<CLCar> getCars(String make, String model, Integer minPrice,
            Integer maxPrice, Integer minYear, Integer maxYear, Integer minMileage,
            Integer maxMileage, Date sListingTime, Date eListingTime, String soldBy,
            boolean includeDeleted, boolean includeFlagged)
    {
        StringBuilder hql = new StringBuilder("select c from CLCar c ");
        boolean firstParam = true;
        if(StringUtils.isNotBlank(make)) {
            hql.append(firstParam ? " where " : " and ").append("make = :make");
            if(firstParam)
                firstParam = false;
        }
        if(StringUtils.isNotBlank(model)) {
            hql.append(firstParam ? " where " : " and ").append("model = :model");
            if(firstParam)
                firstParam = false;
        }
        if(minPrice != null) {
            hql.append(firstParam ? " where " : " and ").append("price >= :minPrice");
            if(firstParam)
                firstParam = false;
        }
        if(maxPrice != null) {
            hql.append(firstParam ? " where " : " and ").append("price <= :maxPrice");
            if(firstParam)
                firstParam = false;
        }
        if(minMileage != null) {
            hql.append(firstParam ? " where " : " and ").append("mileage >= :minMileage");
            if(firstParam)
                firstParam = false;
        }
        if(maxMileage != null) {
            hql.append(firstParam ? " where " : " and ").append("mileage <= :maxMileage");
            if(firstParam)
                firstParam = false;
        }
        if(minYear != null) {
            hql.append(firstParam ? " where " : " and ").append("year >= :minYear");
            if(firstParam)
                firstParam = false;
        }
        if(maxYear != null) {
            hql.append(firstParam ? " where " : " and ").append("year <= :maxYear");
            if(firstParam)
                firstParam = false;
        }
        if(sListingTime != null) {
            hql.append(firstParam ? " where " : " and ").append("listingDate >= :sListingTime");
            if(firstParam)
                firstParam = false;
        }
        if(eListingTime != null) {
            hql.append(firstParam ? " where " : " and ").append("listingDate <= :eListingTime");
            if(firstParam)
                firstParam = false;
        }
        if(StringUtils.isNotBlank(soldBy)) {
            hql.append(firstParam ? " where " : " and ").append("soldBy = :soldBy");
            if(firstParam)
                firstParam = false;
        }

        if(!includeDeleted) {
            hql.append(firstParam ? " where " : " and ").append("deleted = 0");
            if(firstParam)
                firstParam = false;
        }

        if(!includeFlagged) {
            hql.append(firstParam ? " where " : " and ").append("flagged = 0");
        }

        List<CLCar> result = new ArrayList<CLCar>();
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(hql.toString());

        if(StringUtils.isNotBlank(make)) {
            query.setString("make", make);
        }
        if(StringUtils.isNotBlank(model)) {
            query.setString("model", model);
        }
        if(minPrice != null) {
            query.setInteger("minPrice", minPrice);
        }
        if(maxPrice != null) {
            query.setInteger("maxPrice", maxPrice);
        }
        if(minMileage != null) {
            query.setInteger("minMileage", minMileage);
        }
        if(maxMileage != null) {
            query.setInteger("maxMileage", maxMileage);
        }
        if(minYear != null) {
            query.setInteger("minYear", minYear);
        }
        if(maxYear != null) {
            query.setInteger("maxYear", maxYear);
        }
        if(sListingTime != null) {
            query.setTimestamp("sListingTime", sListingTime);
        }
        if(eListingTime != null) {
            query.setTimestamp("eListingTime", eListingTime);
        }
        if(StringUtils.isNotBlank(soldBy)) {
            query.setString("soldBy", soldBy);
        }
        result = query.list();
        tx.commit();

        return result;
    }

    public static Date getMaxListingDate() {
        Date result = null;
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        Query query = session.createQuery(HQL_GET_MAX_LISTING_DATE);
        result = (Date)query.uniqueResult();
        tx.commit();

        return result;
    }

    public void save() throws HibernateException {
        Session session = HibernateUtil.getSession();
        Transaction tx = session.beginTransaction();
        session.saveOrUpdate(this);
        tx.commit();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CLCar other = (CLCar) obj;
        if (this.craigslistId != other.craigslistId && (this.craigslistId == null || !this.craigslistId.equals(other.craigslistId))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.craigslistId != null ? this.craigslistId.hashCode() : 0);
        return hash;
    }
    
}
