module core {
    requires org.hibernate.orm.core;
    requires static lombok;
    requires jakarta.persistence;
    requires java.naming;
    requires com.opencsv;
    requires spring.context;

    exports com.admissionManagement.core.entity;
    exports com.admissionManagement.core.dto;
    exports com.admissionManagement.core.service;
    exports com.admissionManagement.core.util;
    exports com.admissionManagement.core.helper;

    opens com.admissionManagement.core.entity to org.hibernate.orm.core;
    opens com.admissionManagement.core.dto to javafx.base;

    opens com.admissionManagement.core.service to spring.core;
    opens com.admissionManagement.core.dao to spring.core;
}