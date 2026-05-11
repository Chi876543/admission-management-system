package com.admissionManagement.core.service;

import com.admissionManagement.core.dao.DiemThiXetTuyenDAO;
import com.admissionManagement.core.dao.ThiSinhDAO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.DiemThiXetTuyenDTO;
import com.admissionManagement.core.dto.ThongKeDTO;
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
import java.util.ArrayList;
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

            diemThiXetTuyen.setDiemToanVSAT(diemThiXetTuyenDTO.getDiemToanVSAT());
            diemThiXetTuyen.setDiemLyVSAT(diemThiXetTuyenDTO.getDiemLyVSAT());
            diemThiXetTuyen.setDiemHoaVSAT(diemThiXetTuyenDTO.getDiemHoaVSAT());
            diemThiXetTuyen.setDiemSinhVSAT(diemThiXetTuyenDTO.getDiemSinhVSAT());
            diemThiXetTuyen.setDiemSuVSAT(diemThiXetTuyenDTO.getDiemSuVSAT());
            diemThiXetTuyen.setDiemDiaVSAT(diemThiXetTuyenDTO.getDiemDiaVSAT());
            diemThiXetTuyen.setDiemVanVSAT(diemThiXetTuyenDTO.getDiemVanVSAT());
            diemThiXetTuyen.setN1VSAT(diemThiXetTuyenDTO.getN1VSAT());

            diemThiXetTuyen.setNk1(diemThiXetTuyenDTO.getNk1());
            diemThiXetTuyen.setNk2(diemThiXetTuyenDTO.getNk2());
            diemThiXetTuyen.setNk3(diemThiXetTuyenDTO.getNk3());
            diemThiXetTuyen.setNk4(diemThiXetTuyenDTO.getNk4());
            diemThiXetTuyen.setNk5(diemThiXetTuyenDTO.getNk5());
            diemThiXetTuyen.setNk6(diemThiXetTuyenDTO.getNk6());

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

            diemThiXetTuyen.setDiemToanVSAT(newDiemThiXetTuyenDTO.getDiemToanVSAT());
            diemThiXetTuyen.setDiemLyVSAT(newDiemThiXetTuyenDTO.getDiemLyVSAT());
            diemThiXetTuyen.setDiemHoaVSAT(newDiemThiXetTuyenDTO.getDiemHoaVSAT());
            diemThiXetTuyen.setDiemSinhVSAT(newDiemThiXetTuyenDTO.getDiemSinhVSAT());
            diemThiXetTuyen.setDiemSuVSAT(newDiemThiXetTuyenDTO.getDiemSuVSAT());
            diemThiXetTuyen.setDiemDiaVSAT(newDiemThiXetTuyenDTO.getDiemDiaVSAT());
            diemThiXetTuyen.setDiemVanVSAT(newDiemThiXetTuyenDTO.getDiemVanVSAT());
            diemThiXetTuyen.setN1VSAT(newDiemThiXetTuyenDTO.getN1VSAT());

            diemThiXetTuyen.setNk1(newDiemThiXetTuyenDTO.getNk1());
            diemThiXetTuyen.setNk2(newDiemThiXetTuyenDTO.getNk2());
            diemThiXetTuyen.setNk3(newDiemThiXetTuyenDTO.getNk3());
            diemThiXetTuyen.setNk4(newDiemThiXetTuyenDTO.getNk4());
            diemThiXetTuyen.setNk5(newDiemThiXetTuyenDTO.getNk5());
            diemThiXetTuyen.setNk6(newDiemThiXetTuyenDTO.getNk6());

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

    public List<ThongKeDTO> getThongKeDiem(){
        List<ThongKeDTO> listThongKe = new ArrayList<>();

        String sql =
                //THPT
                "SELECT 'THPT' as loai, 'Toán' as mon, COUNT(`TO`), MIN(`TO`), MAX(`TO`), AVG(`TO`) FROM xt_diemthixettuyen WHERE `TO` IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'THPT' as loai, 'Vật lý' as mon, COUNT(LI), MIN(LI), MAX(LI), AVG(LI) FROM xt_diemthixettuyen WHERE LI IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'THPT' as loai, 'Hóa học' as mon, COUNT(HO), MIN(HO), MAX(HO), AVG(HO) FROM xt_diemthixettuyen WHERE HO IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'THPT' as loai, 'Sinh học' as mon, COUNT(SI), MIN(SI), MAX(SI), AVG(SI) FROM xt_diemthixettuyen WHERE SI IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'THPT' as loai, 'Lịch sử' as mon, COUNT(SU), MIN(SU), MAX(SU), AVG(SU) FROM xt_diemthixettuyen WHERE SU IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'THPT' as loai, 'Địa lý' as mon, COUNT(DI), MIN(DI), MAX(DI), AVG(DI) FROM xt_diemthixettuyen WHERE DI IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'THPT' as loai, 'Ngữ văn' as mon, COUNT(VA), MIN(VA), MAX(VA), AVG(VA) FROM xt_diemthixettuyen WHERE VA IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'THPT' as loai, 'Tin học' as mon, COUNT(TI), MIN(TI), MAX(TI), AVG(TI) FROM xt_diemthixettuyen WHERE TI IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'THPT' as loai, 'Ngoại ngữ' as mon, COUNT(N1_THI), MIN(N1_THI), MAX(N1_THI), AVG(N1_THI) FROM xt_diemthixettuyen WHERE N1_THI IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'THPT' as loai, 'Giáo dục Kinh tế và Pháp luât' as mon, COUNT(KTPL), MIN(KTPL), MAX(KTPL), AVG(KTPL) FROM xt_diemthixettuyen WHERE KTPL IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'THPT' as loai, 'Công nghệ Công nghiệp' as mon, COUNT(CNCN), MIN(CNCN), MAX(CNCN), AVG(CNCN) FROM xt_diemthixettuyen WHERE CNCN IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'THPT' as loai, 'Công nghệ Nông nghiệp' as mon, COUNT(CNNN), MIN(CNNN), MAX(CNNN), AVG(CNNN) FROM xt_diemthixettuyen WHERE CNNN IS NOT NULL " +
                "UNION ALL " +
                //VSAT
                "SELECT 'VSAT' as loai, 'Toán' as mon, COUNT(TO_VSAT), MIN(TO_VSAT), MAX(TO_VSAT), AVG(TO_VSAT) FROM xt_diemthixettuyen WHERE TO_VSAT IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'VSAT' as loai, 'Vật lý' as mon, COUNT(LI_VSAT), MIN(LI_VSAT), MAX(LI_VSAT), AVG(LI_VSAT) FROM xt_diemthixettuyen WHERE LI_VSAT IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'VSAT' as loai, 'Hóa học' as mon, COUNT(HO_VSAT), MIN(HO_VSAT), MAX(HO_VSAT), AVG(HO_VSAT) FROM xt_diemthixettuyen WHERE HO_VSAT IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'VSAT' as loai, 'Sinh học' as mon, COUNT(SI_VSAT), MIN(SI_VSAT), MAX(SI_VSAT), AVG(SI_VSAT) FROM xt_diemthixettuyen WHERE SI_VSAT IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'VSAT' as loai, 'Lịch sử' as mon, COUNT(SU_VSAT), MIN(SU_VSAT), MAX(SU_VSAT), AVG(SU_VSAT) FROM xt_diemthixettuyen WHERE SU_VSAT IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'VSAT' as loai, 'Địa lý' as mon, COUNT(DI_VSAT), MIN(DI_VSAT), MAX(DI_VSAT), AVG(DI_VSAT) FROM xt_diemthixettuyen WHERE DI_VSAT IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'VSAT' as loai, 'Ngữ văn' as mon, COUNT(VA_VSAT), MIN(VA_VSAT), MAX(VA_VSAT), AVG(VA_VSAT) FROM xt_diemthixettuyen WHERE VA_VSAT IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'VSAT' as loai, 'Ngoại ngữ' as mon, COUNT(N1_VSAT), MIN(N1_VSAT), MAX(N1_VSAT), AVG(N1_VSAT) FROM xt_diemthixettuyen WHERE N1_VSAT IS NOT NULL " +
                "UNION ALL " +
                //Năng khiếu
                "SELECT 'Năng khiếu' as loai, 'Kể chuyện - Đọc diễn cảm' as mon, COUNT(NK1), MIN(NK1), MAX(NK1), AVG(NK1) FROM xt_diemthixettuyen WHERE NK1 IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'Năng khiếu' as loai, 'Hát - Nhạc' as mon, COUNT(NK2), MIN(NK2), MAX(NK2), AVG(NK2) FROM xt_diemthixettuyen WHERE NK2 IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'Năng khiếu' as loai, 'Hình họa' as mon, COUNT(NK3), MIN(NK3), MAX(NK3), AVG(NK3) FROM xt_diemthixettuyen WHERE NK3 IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'Năng khiếu' as loai, 'Trang trí' as mon, COUNT(NK4), MIN(NK4), MAX(NK4), AVG(NK4) FROM xt_diemthixettuyen WHERE NK4 IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'Năng khiếu' as loai, 'Hát - Nhạc cụ' as mon, COUNT(NK5), MIN(NK5), MAX(NK5), AVG(NK5) FROM xt_diemthixettuyen WHERE NK5 IS NOT NULL " +
                "UNION ALL " +
                "SELECT 'Năng khiếu' as loai, 'Xướng âm - Thẩm âm - Tiết tấu' as mon, COUNT(NK6), MIN(NK6), MAX(NK6), AVG(NK6) FROM xt_diemthixettuyen WHERE NK6 IS NOT NULL " +
                "UNION ALL " +
                //ĐGNL
                "SELECT 'ĐGNL', 'Đánh giá năng lực', COUNT(NL1), MIN(NL1), MAX(NL1), AVG(NL1) FROM xt_diemthixettuyen WHERE NL1 IS NOT NULL";

        try(Session session = factory.openSession()){
            List<Object[]> results = session.createNativeQuery(sql, Object[].class).list();

            for(Object[] row : results){
                listThongKe.add(new ThongKeDTO(
                        row[0].toString(),
                        row[1].toString(),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).doubleValue(),
                        ((Number) row[4]).doubleValue(),
                        ((Number) row[5]).doubleValue()
                ));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return listThongKe;
    }

}
