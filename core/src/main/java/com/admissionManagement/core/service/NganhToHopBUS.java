package com.admissionManagement.core.service;


import com.admissionManagement.core.dao.NganhDAO;
import com.admissionManagement.core.dao.NganhToHopDAO;
import com.admissionManagement.core.dao.NganhToHopDAO;

import com.admissionManagement.core.dao.ToHopMonThiDAO;
import com.admissionManagement.core.dto.BangQuyDoiDTO;
import com.admissionManagement.core.dto.NganhDTO;
import com.admissionManagement.core.dto.NganhToHopDTO;
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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NganhToHopBUS {

    private final NganhToHopDAO dao;
    private final NganhDAO nganhdao;
    private final ToHopMonThiDAO tohopmonthidao;
    private final SessionFactory factory;

    public NganhToHopBUS() {
        this.dao = new NganhToHopDAO();
        this.nganhdao = new NganhDAO();
        this.tohopmonthidao = new ToHopMonThiDAO();
        this.factory = HibernateUtil.getSessionFactory();
    }

    private NganhToHopDTO toDTO(NganhToHop entity){
        return new NganhToHopDTO(
                entity.getId(),
                entity.getNganh().getMaNganh(),
                entity.getToHopMonThi().getMaToHop(),
                entity.getThMon1(),
                entity.getHsMon1(),
                entity.getThMon2(),
                entity.getHsMon2(),
                entity.getThMon3(),
                entity.getHsMon3(),
                entity.getTbKeys(),
                entity.getAnh(),
                entity.getToan(),
                entity.getLy(),
                entity.getHoa(),
                entity.getSinh(),
                entity.getVan(),
                entity.getSu(),
                entity.getDia(),
                entity.getTin(),
                entity.getNk1(),
                entity.getNk2(),
                entity.getNk3(),
                entity.getNk4(),
                entity.getNk5(),
                entity.getNk6(),
                entity.getCncn(),
                entity.getCnnn(),
                entity.getKhac(),
                entity.getKtpl(),
                entity.getDoLech()
        );
    }

    private List<NganhToHopDTO> mapListEntityToListDTO(List<NganhToHop> entities) {
        return entities.stream().map(this::toDTO).toList();
    }

    public String addNganhToHop(NganhToHopDTO nganhToHopDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();

            Nganh nganhGoc = nganhdao.getByMaNganhWithSession(session, nganhToHopDTO.getMaNganh());
            ToHopMonThi toHopGoc = tohopmonthidao.getByMaToHopWithSession(session, nganhToHopDTO.getMaToHop());
            if (nganhGoc == null) {
                return "Lỗi: Không tìm thấy Ngành có mã " + nganhToHopDTO.getMaNganh();
            }
            if (toHopGoc == null) {
                return "Lỗi: Không tìm thấy Tổ hợp có mã " + nganhToHopDTO.getMaToHop();
            }

            NganhToHop nganhToHop = new NganhToHop();
            nganhToHop.setNganh(nganhGoc);
            nganhToHop.setToHopMonThi(toHopGoc);
            nganhToHop.setHsMon1(nganhToHopDTO.getHsMon1());
            nganhToHop.setHsMon2(nganhToHopDTO.getHsMon2());
            nganhToHop.setHsMon3(nganhToHopDTO.getHsMon3());
            nganhToHop.setThMon1(nganhToHopDTO.getThMon1());
            nganhToHop.setThMon2(nganhToHopDTO.getThMon2());
            nganhToHop.setThMon3(nganhToHopDTO.getThMon3());
            nganhToHop.setTbKeys(nganhToHopDTO.getTbKeys());
            nganhToHop.setAnh(nganhToHopDTO.getAnh());
            nganhToHop.setToan(nganhToHopDTO.getToan());
            nganhToHop.setLy(nganhToHopDTO.getLy());
            nganhToHop.setHoa(nganhToHopDTO.getHoa());
            nganhToHop.setSinh(nganhToHopDTO.getSinh());
            nganhToHop.setVan(nganhToHopDTO.getVan());
            nganhToHop.setSu(nganhToHopDTO.getSu());
            nganhToHop.setDia(nganhToHopDTO.getDia());
            nganhToHop.setTin(nganhToHopDTO.getTin());
            nganhToHop.setKhac(nganhToHopDTO.getKhac());
            nganhToHop.setKtpl(nganhToHopDTO.getKtpl());
            nganhToHop.setDoLech(nganhToHopDTO.getDoLech());

            dao.addWithSession(session, nganhToHop);

            tx.commit();
            return "Added successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String importCsvData(File file) {
        int batchSize = 1000;
        int successCount = 0;

        try (Reader reader = Files.newBufferedReader(file.toPath());
             CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
             Session session = HibernateUtil.getSessionFactory().openSession()) {

            Map<String, Nganh> cacheNganh = nganhdao.getAllWithSession(session).stream()
                    .collect(Collectors.toMap(Nganh::getMaNganh, n -> n));
            Map<String, ToHopMonThi> cacheToHop = tohopmonthidao.getAllWithSession(session).stream()
                    .collect(Collectors.toMap(ToHopMonThi::getMaToHop, t -> t));

            String[] line;
            Transaction tx = session.beginTransaction();

            while ((line = csvReader.readNext()) != null) {
                try {
                    NganhToHop entity = new NganhToHop();
                    Nganh nganh = cacheNganh.get(line[1].trim());
                    entity.setNganh(nganh);
                    ToHopMonThi toHop = cacheToHop.get(line[5].trim());
                    entity.setToHopMonThi(toHop);
                    entity.setTbKeys(line[4].trim());
                    String cleanedValue = line[7].trim().replace(",", ".");
                    entity.setDoLech(BigDecimal.valueOf(Long.parseLong(cleanedValue)));

                    int viTriMoNgoac = line[3].trim().indexOf("(");
                    int viTriDongNgoac = line[3].trim().indexOf(")");

                    if (viTriMoNgoac == -1 || viTriDongNgoac == -1) {
                        continue;
                    }
                    String phanLoi = line[3].trim().substring(viTriMoNgoac + 1, viTriDongNgoac).trim();
                    String[] cacMon = phanLoi.split(",");
                    if (cacMon.length < 3) {
                        continue;
                    }
                    String mon1 = cacMon[0].split("-")[0].trim();
                    String mon2 = cacMon[1].split("-")[0].trim();
                    String mon3 = cacMon[2].split("-")[0].trim();
                    String hsmon1 = cacMon[0].split("-")[1].trim();
                    String hsmon2 = cacMon[1].split("-")[1].trim();
                    String hsmon3 = cacMon[2].split("-")[1].trim();

                    entity.setThMon1(mon1);
                    entity.setHsMon1(Byte.valueOf(hsmon1));
                    entity.setThMon2(mon2);
                    entity.setHsMon2(Byte.valueOf(hsmon2));
                    entity.setThMon3(mon3);
                    entity.setHsMon3(Byte.valueOf(hsmon3));

                    session.persist(entity);
                    successCount++;

                    if (successCount % batchSize == 0) {
                        tx.commit();
                        session.clear();
                        tx = session.beginTransaction();
                    }
                } catch (Exception e) {
                    // Bỏ qua dòng lỗi (hoặc ghi log ra file TXT để sau này báo cáo)
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

    public NganhToHopDTO getNganhToHop(int id){
        try(Session session = factory.openSession()){
            return toDTO(dao.getWithSession(session, id));
        }
    }

    public List<NganhToHopDTO> getAllNganhToHop(){
        Session session = factory.openSession();
        List<NganhToHop> listNganhToHop = dao.getAllWithSession(session);
        session.close();
        return mapListEntityToListDTO(listNganhToHop);
    }

    public String updateNganhToHop(int id, NganhToHopDTO newNganhToHopDTO){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            Nganh nganhGoc = nganhdao.getByMaNganhWithSession(session, newNganhToHopDTO.getMaNganh());
            ToHopMonThi toHopGoc = tohopmonthidao.getByMaToHopWithSession(session, newNganhToHopDTO.getMaToHop());
            if (nganhGoc == null) {
                return "Lỗi: Không tìm thấy Ngành có mã " + newNganhToHopDTO.getMaNganh();
            }
            if (toHopGoc == null) {
                return "Lỗi: Không tìm thấy Tổ hợp có mã " + newNganhToHopDTO.getMaToHop();
            }

            NganhToHop nganhToHop = dao.getWithSession(session, id);
            if(nganhToHop == null){
                return "Lỗi: Không tìm thấy Ngành tổ hợp với ID " + id;
            }

            nganhToHop.setNganh(nganhGoc);
            nganhToHop.setToHopMonThi(toHopGoc);
            nganhToHop.setHsMon1(newNganhToHopDTO.getHsMon1());
            nganhToHop.setHsMon2(newNganhToHopDTO.getHsMon2());
            nganhToHop.setHsMon3(newNganhToHopDTO.getHsMon3());
            nganhToHop.setThMon1(newNganhToHopDTO.getThMon1());
            nganhToHop.setThMon2(newNganhToHopDTO.getThMon2());
            nganhToHop.setThMon3(newNganhToHopDTO.getThMon3());
            nganhToHop.setTbKeys(newNganhToHopDTO.getTbKeys());
            nganhToHop.setAnh(newNganhToHopDTO.getAnh());
            nganhToHop.setToan(newNganhToHopDTO.getToan());
            nganhToHop.setLy(newNganhToHopDTO.getLy());
            nganhToHop.setHoa(newNganhToHopDTO.getHoa());
            nganhToHop.setSinh(newNganhToHopDTO.getSinh());
            nganhToHop.setVan(newNganhToHopDTO.getVan());
            nganhToHop.setSu(newNganhToHopDTO.getSu());
            nganhToHop.setDia(newNganhToHopDTO.getDia());
            nganhToHop.setTin(newNganhToHopDTO.getTin());
            nganhToHop.setKhac(newNganhToHopDTO.getKhac());
            nganhToHop.setKtpl(newNganhToHopDTO.getKtpl());
            nganhToHop.setDoLech(newNganhToHopDTO.getDoLech());

            dao.updateWithSession(session, nganhToHop);

            tx.commit();
            return "Updated successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String deleteNganhToHop(int id){
        Transaction tx = null;
        try(Session session = factory.openSession()) {
            tx = session.beginTransaction();
            NganhToHop nganhToHop = dao.getWithSession(session, id);

            if(nganhToHop == null){
                return "Lỗi: Không tìm thấy Ngành tổ hợp với ID " + id;
            }

            dao.deleteWithSession(session, nganhToHop);

            tx.commit();
            return "Deleted successfully";
        } catch (Exception e) {
            if(tx != null) tx.rollback();
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

}
