module core {
    requires org.hibernate.orm.core;
    requires static lombok;
    requires jakarta.persistence;
    exports com.admissionManagement.core.dto;
    requires java.naming;
}