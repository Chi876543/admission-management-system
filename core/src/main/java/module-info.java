module core {
    requires org.hibernate.orm.core;
    requires static lombok;
    requires jakarta.persistence;
    requires java.naming;

    exports com.admissionManagement.core.entity;
    exports com.admissionManagement.core.dto;
    exports com.admissionManagement.core.service;
    exports com.admissionManagement.core.util;
    exports com.admissionManagement.core.helper;

    opens com.admissionManagement.core.entity to org.hibernate.orm.core;
    opens com.admissionManagement.core.dto to javafx.base;
}