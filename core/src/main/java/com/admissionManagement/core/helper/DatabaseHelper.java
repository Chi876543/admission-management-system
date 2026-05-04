package com.admissionManagement.core.helper;

import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.function.Function;
import com.admissionManagement.core.util.HibernateUtil;

public class DatabaseHelper {

    /**
     * Hàm Batch Insert dùng chung cho mọi bảng
     * @param listDTO: Danh sách dữ liệu DTO nhận từ giao diện
     * @param mapper: Hàm chuyển đổi (Lambda) từ DTO sang Entity tương ứng
     * @param <D>: Kiểu dữ liệu của DTO
     * @param <E>: Kiểu dữ liệu của Entity
     */
    public static <D, E> String importBatch(List<D> listDTO, Function<D, E> mapper) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            int batchSize = 50;

            for (int i = 0; i < listDTO.size(); i++) {
                D dto = listDTO.get(i);

                E entity = mapper.apply(dto);

                if (entity != null) {
                    session.persist(entity);
                }

                if (i > 0 && i % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }

            tx.commit();
            return "Import thành công " + listDTO.size() + " bản ghi vào cơ sở dữ liệu.";

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return "Lỗi Import Database: " + e.getMessage();
        }
    }
}
