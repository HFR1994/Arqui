package com.itesm.arqui.googlemaps.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.TimeZone;

public abstract class Entity<T>{

    private Long id;

    private String uuid;

    private Boolean status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateCreated;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public abstract Class<T> findDomainClass();

    @Override
    public boolean equals(Object o) {

        Class<T> clase = findDomainClass();

        if (o == this) return true;

        if (!clase.isInstance(o)) {
            return false;
        }

        T obj = clase.cast(o);

        EqualsBuilder equals = new EqualsBuilder();

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                equals.append(PropertyUtils.getProperty(this, field.getName()),field.get(obj));
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }


        return equals.isEquals();
    }

    @Override
    public int hashCode() {
        Field[] fields = this.getClass().getDeclaredFields();
        HashCodeBuilder var = new HashCodeBuilder(17, 37);
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                var.append(field.get(this.getClass()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return var.toHashCode();
    }

    Date parseDate(Date date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));

        String dateInString = formatter.format(date)+"T00:00:00Z";

        Instant instant = Instant.parse(dateInString);
        LocalDateTime result = LocalDateTime.ofInstant(instant, ZoneId.of(ZoneOffset.UTC.getId()));

        return Date.from(result.atZone(ZoneId.systemDefault()).toInstant());

    }

}
