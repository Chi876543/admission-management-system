package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.DiemThiXetTuyenDAO;
import com.admissionManagement.core.dao.ThiSinhDAO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.DiemThiXetTuyenDTO;
import com.admissionManagement.core.entity.BangQuyDoi;
import com.admissionManagement.core.entity.DiemThiXetTuyen;
import com.admissionManagement.core.entity.ThiSinh;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiemThiXetTuyenBUS {
    private final DiemThiXetTuyenDAO dao;
    private final ThiSinhDAO thisinhdao;
    private final SessionFactory factory;

    public DiemThiXetTuyenBUS() {
        this.dao = new DiemThiXetTuyenDAO();
        this.thisinhdao = new ThiSinhDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private DiemThiXetTuyenDTO toDTO(DiemThiXetTuyen entity){
        return new DiemThiXetTuyenDTO(
                entity.getIdDiemThi(),
                entity.getThiSinh().getCccd(),
                entity.getSoBaoDanh(),
                entity.getPhuongThuc(),
                entity.getDiemToan(),
                entity.getDiemLy(),
                entity.getDiemHoa(),
                entity.getDiemSinh(),
                entity.getDiemSu(),
                entity.getDiemDia(),
                entity.getDiemVan(),
                entity.getDiemTin(),
                entity.getDiemKtpl(),
                entity.getN1Thi(),
                entity.getN1Cc(),
                entity.getCncn(),
                entity.getCnnn(),
                entity.getNl1(),
                entity.getNk1(),
                entity.getNk2(),
                entity.getNk3(),
                entity.getNk4(),
                entity.getNk5(),
                entity.getNk6(),
                entity.getDiemToanVSAT(),
                entity.getDiemLyVSAT(),
                entity.getDiemHoaVSAT(),
                entity.getDiemSinhVSAT(),
                entity.getDiemSuVSAT(),
                entity.getDiemDiaVSAT(),
                entity.getDiemVanVSAT(),
                entity.getN1VSAT()
        );
    }

    private List<DiemThiXetTuyenDTO> mapListEntityToListDTO(List<DiemThiXetTuyen> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addDiemThiXetTuyen(DiemThiXetTuyenDTO diemThiXetTuyenDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, diemThiXetTuyenDTO.getCccd());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + diemThiXetTuyenDTO.getCccd();
            }

            DiemThiXetTuyen diemThiXetTuyen = new DiemThiXetTuyen();
            diemThiXetTuyen.setThiSinh(thiSinhGoc);
            diemThiXetTuyen.setSoBaoDanh(diemThiXetTuyenDTO.getSoBaoDanh());
            diemThiXetTuyen.setPhuongThuc(diemThiXetTuyenDTO.getPhuongThuc());
            diemThiXetTuyen.setDiemToan(diemThiXetTuyenDTO.getDiemToan());
            diemThiXetTuyen.setDiemLy(diemThiXetTuyenDTO.getDiemLy());
            diemThiXetTuyen.setDiemHoa(diemThiXetTuyenDTO.getDiemHoa());
            diemThiXetTuyen.setDiemSinh(diemThiXetTuyenDTO.getDiemSinh());
            diemThiXetTuyen.setDiemSu(diemThiXetTuyenDTO.getDiemSu());
            diemThiXetTuyen.setDiemDia(diemThiXetTuyenDTO.getDiemDia());
            diemThiXetTuyen.setDiemVan(diemThiXetTuyenDTO.getDiemVan());
            diemThiXetTuyen.setDiemTin(diemThiXetTuyenDTO.getDiemTin());
            diemThiXetTuyen.setDiemKtpl(diemThiXetTuyenDTO.getDiemKtpl());
            diemThiXetTuyen.setN1Thi(diemThiXetTuyenDTO.getN1Thi());
            diemThiXetTuyen.setN1Cc(diemThiXetTuyenDTO.getN1Cc());
            diemThiXetTuyen.setCncn(diemThiXetTuyenDTO.getCncn());
            diemThiXetTuyen.setCnnn(diemThiXetTuyenDTO.getCnnn());
            diemThiXetTuyen.setNl1(diemThiXetTuyenDTO.getNl1());
            diemThiXetTuyen.setNk1(diemThiXetTuyenDTO.getNk1());
            diemThiXetTuyen.setNk2(diemThiXetTuyenDTO.getNk2());

            dao.addWithSession(session, diemThiXetTuyen);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String importVsatCsvData(File file) {
        int batchSize = 1000;
        int successCount = 0;

        try (Reader reader = Files.newBufferedReader(file.toPath());
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
             Session session = HibernateUtil.getSessionFactory().openSession()) {

            String[] line;
            Transaction tx = session.beginTransaction();

            Map<String, DiemThiXetTuyen> diemCache = new HashMap<>();

            while ((line = csvReader.readNext()) != null) {
                try {
                    String cccd = line[1].trim();
                    DiemThiXetTuyen diem = diemCache.get(cccd);
                    if (diem == null) {
                        diem = dao.getByCccdWithSession(session, cccd);
                        if (diem != null) {
                            diemCache.put(cccd, diem);
                        }
                    }

                    if (diem != null) {
                        String cleanMonHoc = DatabaseHelper.lamSachTenMon(line[7].trim());
                        String cleanedValue = line[8].trim().replace(",", ".");
                        BigDecimal diemMoi = new BigDecimal(cleanedValue);

                        if (diem.getDiemToanVSAT() == null) diem.setDiemToanVSAT(BigDecimal.ZERO);
                        if (diem.getDiemLyVSAT() == null) diem.setDiemLyVSAT(BigDecimal.ZERO);
                        if (diem.getDiemVanVSAT() == null) diem.setDiemVanVSAT(BigDecimal.ZERO);
                        if (diem.getDiemHoaVSAT() == null) diem.setDiemHoaVSAT(BigDecimal.ZERO);
                        if (diem.getDiemSinhVSAT() == null) diem.setDiemSinhVSAT(BigDecimal.ZERO);
                        if (diem.getDiemSuVSAT() == null) diem.setDiemSuVSAT(BigDecimal.ZERO);
                        if (diem.getDiemDiaVSAT() == null) diem.setDiemDiaVSAT(BigDecimal.ZERO);
                        if (diem.getN1VSAT() == null) diem.setN1VSAT(BigDecimal.ZERO);

                        if (cleanMonHoc.contains("toan")) {
                            if (diem.getDiemToanVSAT().compareTo(diemMoi) < 0)
                                diem.setDiemToanVSAT(diemMoi);
                        } else if (cleanMonHoc.contains("ly") || cleanMonHoc.contains("li")) {
                            if (diem.getDiemLyVSAT().compareTo(diemMoi) < 0)
                                diem.setDiemLyVSAT(diemMoi);
                        } else if (cleanMonHoc.contains("van")) {
                            if (diem.getDiemVanVSAT().compareTo(diemMoi) < 0)
                                diem.setDiemVanVSAT(diemMoi);
                        } else if (cleanMonHoc.contains("hoa")) {
                            if (diem.getDiemHoaVSAT().compareTo(diemMoi) < 0)
                                diem.setDiemHoaVSAT(diemMoi);
                        } else if (cleanMonHoc.contains("sinh")) {
                            if (diem.getDiemSinhVSAT().compareTo(diemMoi) < 0)
                                diem.setDiemSinhVSAT(diemMoi);
                        } else if (cleanMonHoc.contains("su")) {
                            if (diem.getDiemSuVSAT().compareTo(diemMoi) < 0)
                                diem.setDiemSuVSAT(diemMoi);
                        } else if (cleanMonHoc.contains("dia")) {
                            if (diem.getDiemDiaVSAT().compareTo(diemMoi) < 0)
                                diem.setDiemDiaVSAT(diemMoi);
                        } else if (cleanMonHoc.contains("anh")) {
                            if (diem.getN1VSAT().compareTo(diemMoi) < 0)
                                diem.setN1VSAT(diemMoi);
                        }
                    }

                    successCount++;

                    if (successCount % batchSize == 0) {
                        tx.commit();
                        session.clear();
                        diemCache.clear();
                        tx = session.beginTransaction();
                    }
                } catch (Exception e) {
                    // Bỏ qua dòng lỗi
                }
            }

            if (tx.isActive()) {
                tx.commit();
            }

            return "Import thành công " + successCount + " bản ghi!";

        } catch (Exception e) {
            return "Lỗi đọc file: " + e.getMessage();
        }
    }

    public String importDGNLCsvData(File file) {
        int batchSize = 1000;
        int successCount = 0;

        try (Reader reader = Files.newBufferedReader(file.toPath());
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
             Session session = HibernateUtil.getSessionFactory().openSession()) {

            String[] line;
            Transaction tx = session.beginTransaction();

            Map<String, DiemThiXetTuyen> diemCache = new HashMap<>();

            while ((line = csvReader.readNext()) != null) {
                try {
                    String cccd = line[1].trim();
                    DiemThiXetTuyen diem = diemCache.get(cccd);
                    if (diem == null) {
                        diem = dao.getByCccdWithSession(session, cccd);
                        if (diem != null) {
                            diemCache.put(cccd, diem);
                        }
                    }

                    if (diem != null) {
                        BigDecimal diemMoi = new BigDecimal(line[8].trim());
                        if (diem.getNl1() == null) {
                            diem.setNl1(BigDecimal.ZERO);
                        }
                        if (diem.getNl1().compareTo(diemMoi) < 0) {
                            diem.setNl1(diemMoi);
                        }
                    }

                    successCount++;

                    if (successCount % batchSize == 0) {
                        tx.commit();
                        session.clear();
                        diemCache.clear();
                        tx = session.beginTransaction();
                    }
                } catch (Exception e) {
                    // Bỏ qua dòng lỗi
                }
            }

            if (tx.isActive()) {
                tx.commit();
            }

            return "Import thành công " + successCount + " bản ghi!";

        } catch (Exception e) {
            return "Lỗi đọc file: " + e.getMessage();
        }
    }

    public DiemThiXetTuyenDTO getDiemThiXetTuyen(int id){
        try(Session session = factory.openSession()){
            return toDTO(dao.getWithSession(session, id));
        }
    }

    public List<DiemThiXetTuyenDTO> getAllDiemThiXetTuyen(){
        Session session = factory.openSession();
        List<DiemThiXetTuyen> listDiemThiXetTuyen = dao.getAllWithSession(session);
        session.close();
        return mapListEntityToListDTO(listDiemThiXetTuyen);
    }

    public String updateDiemThiXetTuyen(int id, DiemThiXetTuyenDTO newDiemThiXetTuyenDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            ThiSinh thiSinhGoc = thisinhdao.getByCccdWithSesstion(session, newDiemThiXetTuyenDTO.getCccd());
            if (thiSinhGoc == null) {
                return "Lỗi: Không tìm thấy Thí sinh có cccd " + newDiemThiXetTuyenDTO.getCccd();
            }

            DiemThiXetTuyen diemThiXetTuyen = dao.getWithSession(session, id);

            if(diemThiXetTuyen == null){
                return "Lỗi: Không tìm thấy điểm thi với ID " + id;
            }

            diemThiXetTuyen.setThiSinh(thiSinhGoc);
            diemThiXetTuyen.setSoBaoDanh(newDiemThiXetTuyenDTO.getSoBaoDanh());
            diemThiXetTuyen.setPhuongThuc(newDiemThiXetTuyenDTO.getPhuongThuc());
            diemThiXetTuyen.setDiemToan(newDiemThiXetTuyenDTO.getDiemToan());
            diemThiXetTuyen.setDiemLy(newDiemThiXetTuyenDTO.getDiemLy());
            diemThiXetTuyen.setDiemHoa(newDiemThiXetTuyenDTO.getDiemHoa());
            diemThiXetTuyen.setDiemSinh(newDiemThiXetTuyenDTO.getDiemSinh());
            diemThiXetTuyen.setDiemSu(newDiemThiXetTuyenDTO.getDiemSu());
            diemThiXetTuyen.setDiemDia(newDiemThiXetTuyenDTO.getDiemDia());
            diemThiXetTuyen.setDiemVan(newDiemThiXetTuyenDTO.getDiemVan());
            diemThiXetTuyen.setDiemTin(newDiemThiXetTuyenDTO.getDiemTin());
            diemThiXetTuyen.setDiemKtpl(newDiemThiXetTuyenDTO.getDiemKtpl());
            diemThiXetTuyen.setN1Thi(newDiemThiXetTuyenDTO.getN1Thi());
            diemThiXetTuyen.setN1Cc(newDiemThiXetTuyenDTO.getN1Cc());
            diemThiXetTuyen.setCncn(newDiemThiXetTuyenDTO.getCncn());
            diemThiXetTuyen.setCnnn(newDiemThiXetTuyenDTO.getCnnn());
            diemThiXetTuyen.setNl1(newDiemThiXetTuyenDTO.getNl1());
            diemThiXetTuyen.setNk1(newDiemThiXetTuyenDTO.getNk1());
            diemThiXetTuyen.setNk2(newDiemThiXetTuyenDTO.getNk2());

            dao.updateWithSession(session, diemThiXetTuyen);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteDiemThiXetTuyen(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            DiemThiXetTuyen diemThiXetTuyen = dao.getWithSession(session, id);

            if(diemThiXetTuyen == null){
                return "Lỗi: Không tìm thấy điểm thi với ID " + id;
            }

            dao.deleteWithSession(session, diemThiXetTuyen);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

}
