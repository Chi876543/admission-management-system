package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.*;
import com.admissionManagement.core.dto.DiemCongXetTuyenDTO;
import com.admissionManagement.core.entity.*;
import com.admissionManagement.core.helper.DatabaseHelper;
import com.admissionManagement.core.util.HibernateUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiemCongXetTuyenBUS {
    private final DiemCongXetTuyenDAO dao;
    private final ThiSinhDAO thisinhdao;
    private final NganhDAO nganhdao;
    private final ToHopMonThiDAO tohopdao;
    private final SessionFactory factory;
    private final NganhToHopDAO nganhtohopdao;

    public DiemCongXetTuyenBUS() {
        this.dao = new DiemCongXetTuyenDAO();
        this.thisinhdao = new ThiSinhDAO();
        this.nganhdao = new NganhDAO();
        this.tohopdao = new ToHopMonThiDAO();
        this.nganhtohopdao = new NganhToHopDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private DiemCongXetTuyenDTO toDTO(DiemCongXetTuyen entity){
        return new DiemCongXetTuyenDTO(
                entity.getIdDiemCong(),
                entity.getThiSinh().getCccd(),
                entity.getNganh().getMaNganh(),
                entity.getToHopMonThi().getMaToHop(),
                entity.getPhuongThuc(),
                entity.getDiemCC(),
                entity.getDiemUtxt(),
                entity.getDiemTong(),
                entity.getGhiChu(),
                entity.getDcKeys()
        );
    }

    private List<DiemCongXetTuyenDTO> mapListEntityToListDTO(List<DiemCongXetTuyen> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addDiemCongXetTuyen(DiemCongXetTuyenDTO diemCongXetTuyenDTO){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, diemCongXetTuyenDTO.getTsCccd());
            Nganh nganhGoc = nganhdao.getByMaNganhWithSession(session, diemCongXetTuyenDTO.getMaNganh());
            ToHopMonThi toHopGoc = tohopdao.getByMaToHopWithSession(session, diemCongXetTuyenDTO.getMaToHop());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + diemCongXetTuyenDTO.getTsCccd();
            }
            if (toHopGoc == null) {
                return "Lỗi: Không tìm thấy Tổ hợp có mã " + diemCongXetTuyenDTO.getMaToHop();
            }
            if (nganhGoc == null) {
                return "Lỗi: Không tìm thấy Ngành có mã " + diemCongXetTuyenDTO.getMaNganh();
            }

            DiemCongXetTuyen diemCongXetTuyen = new DiemCongXetTuyen();
            diemCongXetTuyen.setThiSinh(thiSinhGoc);
            diemCongXetTuyen.setNganh(nganhGoc);
            diemCongXetTuyen.setToHopMonThi(toHopGoc);
            diemCongXetTuyen.setPhuongThuc(diemCongXetTuyenDTO.getPhuongThuc());
            diemCongXetTuyen.setDiemCC(diemCongXetTuyenDTO.getDiemCC());
            diemCongXetTuyen.setDiemUtxt(diemCongXetTuyenDTO.getDiemUtxt());
            diemCongXetTuyen.setDiemTong(diemCongXetTuyenDTO.getDiemTong());
            diemCongXetTuyen.setGhiChu(diemCongXetTuyenDTO.getGhiChu());
            diemCongXetTuyen.setDcKeys(diemCongXetTuyenDTO.getDcKeys());

            dao.addWithSession(session, diemCongXetTuyen);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        } finally {
            session.close();
        }
    }

    public String importUtxtCsvData(File file) {
        int batchSize = 1000;
        int successCount = 0;

        try (Reader reader = Files.newBufferedReader(file.toPath());
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
             Session session = HibernateUtil.getSessionFactory().openSession()) {

            Map<String, NganhToHop> cacheNganhToHop = nganhtohopdao.getAllWithSession(session).stream()
                    .collect(Collectors.toMap(NganhToHop::getTbKeys, n -> n));
            Map<String, ThiSinh> cacheThiSinh = thisinhdao.getAll(session).stream()
                    .collect(Collectors.toMap(ThiSinh::getCccd, n -> n));

            String[] line;
            Transaction tx = session.beginTransaction();

            while ((line = csvReader.readNext()) != null) {
                try {
                    ThiSinh thiSinh = cacheThiSinh.get(line[1].trim());
                    if (thiSinh == null) {
                        continue;
                    }

                    String ghiChu = line[5].trim() + " - " + line[3].trim() + " cấp " + line[2].trim();
                    String monDoatGiai = DatabaseHelper.dichTenMon(line[4].trim());

                    BigDecimal diemDoatGiai = new BigDecimal(line[6].trim().replace(",", "."));
                    BigDecimal diemKhongDoatGiai = new BigDecimal(line[7].trim().replace(",", "."));

                    for (NganhToHop nganhToHop : cacheNganhToHop.values()) {
                        DiemCongXetTuyen entity = new DiemCongXetTuyen();

                        entity.setThiSinh(thiSinh);
                        entity.setGhiChu(ghiChu);
                        entity.setToHopMonThi(nganhToHop.getToHopMonThi());
                        entity.setNganh(nganhToHop.getNganh());
                        entity.setPhuongThuc("THPT");
                        entity.setDcKeys(thiSinh.getCccd() + "_" + nganhToHop.getNganh().getMaNganh() + "_" + nganhToHop.getToHopMonThi().getMaToHop());
                        entity.setDiemCC(BigDecimal.ZERO);

                        String mon1 = DatabaseHelper.dichTenMon(nganhToHop.getThMon1());
                        String mon2 = DatabaseHelper.dichTenMon(nganhToHop.getThMon2());
                        String mon3 = DatabaseHelper.dichTenMon(nganhToHop.getThMon3());

                        if (monDoatGiai.equals(mon1) || monDoatGiai.equals(mon2) || monDoatGiai.equals(mon3)) {
                            entity.setDiemUtxt(diemDoatGiai);
                            entity.setDiemTong(diemDoatGiai);
                        } else {
                            entity.setDiemUtxt(diemKhongDoatGiai);
                            entity.setDiemTong(diemKhongDoatGiai);
                        }

                        session.persist(entity);
                        successCount++;

                        if (successCount % batchSize == 0) {
                            tx.commit();
                            session.clear();
                            tx = session.beginTransaction();
                        }
                    }
                } catch (Exception e) {
                    // System.out.println("Lỗi dòng CSV: " + String.join(",", line));
                }
            }

            if (tx.isActive()) {
                tx.commit();
            }

            return "Import thành công " + successCount + " bản ghi điểm UTXT!";

        } catch (Exception e) {
            return "Lỗi đọc file: " + e.getMessage();
        }
    }

    public DiemCongXetTuyenDTO getDiemCongXetTuyen(int id){
        try(Session session = factory.openSession()){
            return toDTO(dao.getWithSession(session, id));
        }
    }

    public List<DiemCongXetTuyenDTO> getAllDiemCongXetTuyen(){
        Session session = factory.openSession();
        List<DiemCongXetTuyen> listDiemCongXetTuyen = dao.getAllWithSession(session);
        session.close();
        return mapListEntityToListDTO(listDiemCongXetTuyen);
    }

    public String updateDiemCongXetTuyen(int id, DiemCongXetTuyenDTO newDiemCongXetTuyenDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, newDiemCongXetTuyenDTO.getTsCccd());
            Nganh nganhGoc = nganhdao.getByMaNganhWithSession(session, newDiemCongXetTuyenDTO.getMaNganh());
            ToHopMonThi toHopGoc = tohopdao.getByMaToHopWithSession(session, newDiemCongXetTuyenDTO.getMaToHop());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + newDiemCongXetTuyenDTO.getTsCccd();
            }
            if (toHopGoc == null) {
                return "Lỗi: Không tìm thấy Tổ hợp có mã " + newDiemCongXetTuyenDTO.getMaToHop();
            }
            if (nganhGoc == null) {
                return "Lỗi: Không tìm thấy Ngành có mã " + newDiemCongXetTuyenDTO.getMaNganh();
            }

            DiemCongXetTuyen diemCongXetTuyen = dao.getWithSession(session, id);

            if(diemCongXetTuyen == null){
                return "Lỗi: Không tìm thấy bảng điểm cộng với ID " + id;
            }

            diemCongXetTuyen.setThiSinh(thiSinhGoc);
            diemCongXetTuyen.setNganh(nganhGoc);
            diemCongXetTuyen.setToHopMonThi(toHopGoc);
            diemCongXetTuyen.setPhuongThuc(newDiemCongXetTuyenDTO.getPhuongThuc());
            diemCongXetTuyen.setDiemCC(newDiemCongXetTuyenDTO.getDiemCC());
            diemCongXetTuyen.setDiemUtxt(newDiemCongXetTuyenDTO.getDiemUtxt());
            diemCongXetTuyen.setDiemTong(newDiemCongXetTuyenDTO.getDiemTong());
            diemCongXetTuyen.setGhiChu(newDiemCongXetTuyenDTO.getGhiChu());
            diemCongXetTuyen.setDcKeys(newDiemCongXetTuyenDTO.getDcKeys());

            dao.updateWithSession(session, diemCongXetTuyen);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteDiemCongXetTuyen(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            DiemCongXetTuyen diemCongXetTuyen = dao.getWithSession(session, id);

            if(diemCongXetTuyen == null){
                return "Lỗi: Không tìm thấy bảng điểm cộng với ID " + id;
            }

            dao.deleteWithSession(session, diemCongXetTuyen);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }
}
