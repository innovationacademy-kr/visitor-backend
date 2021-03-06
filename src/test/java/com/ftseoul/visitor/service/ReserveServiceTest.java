package com.ftseoul.visitor.service;

import com.ftseoul.visitor.data.Reserve;
import com.ftseoul.visitor.data.ReserveRepository;
import com.ftseoul.visitor.data.Staff;
import com.ftseoul.visitor.data.StaffRepository;
import com.ftseoul.visitor.data.Visitor;
import com.ftseoul.visitor.data.VisitorRepository;
import com.ftseoul.visitor.dto.payload.Response;
import com.ftseoul.visitor.dto.reserve.ReserveListResponseDto;
import com.ftseoul.visitor.dto.reserve.ReserveModifyDto;
import com.ftseoul.visitor.dto.reserve.ReserveRequestDto;
import com.ftseoul.visitor.dto.reserve.ReserveVisitorDto;
import com.ftseoul.visitor.dto.visitor.VisitorDto;
import com.ftseoul.visitor.dto.visitor.VisitorModifyDto;
import com.ftseoul.visitor.encrypt.Seed;
import com.ftseoul.visitor.exception.PhoneDuplicatedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Rollback(value = true)
class ReserveServiceTest {

    @Autowired
    private Seed seed;

    @Autowired
    private ReserveRepository reserveRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private ReserveService reserveService;

    @Autowired
    private EntityManager em;

    Staff savedStaff;

    Reserve savedReserve;

    Visitor savedVisitor;

    @BeforeAll
    void setup() {
        init();
    }

    @AfterAll
    void cleanup() {
        finish();
    }

    void init() {
        Staff staff = Staff
            .builder()
            .name(seed.encrypt("abcde"))
            .phone(seed.encrypt("01012345678"))
            .department("????????????")
            .build();
        savedStaff = staffRepository.save(staff);

        Reserve reserve = Reserve
            .builder()
            .targetStaff(savedStaff.getId())
            .purpose("??????")
            .date(LocalDateTime.now())
            .place("??????")
            .build();
        savedReserve = reserveRepository.save(reserve);

        Visitor visitor = Visitor
            .builder()
            .name(seed.encrypt("????????????"))
            .organization("???????????????????????????")
            .reserve_id(savedReserve.getId())
            .phone(seed.encrypt("01011112222"))
            .build();
        savedVisitor = visitorRepository.save(visitor);
    }

    void finish() {
        reserveRepository.delete(savedReserve);
        visitorRepository.delete(savedVisitor);
        staffRepository.delete(savedStaff);
    }


    @Test
    @Transactional
    void ???????????????_??????() {
        long id = savedReserve.getId();
        ReserveListResponseDto result = reserveService.findById(id);
        assertEquals(result.getId(), id);
    }

    @Test
    @Transactional
    void ???????????????_????????????() {
        List<VisitorDto> visitors = new ArrayList<>();
        String phoneNumber = "01012345678";
        VisitorDto v1 = new VisitorDto("?????????1", phoneNumber, "42");
        VisitorDto v2 = new VisitorDto("?????????2", phoneNumber, "42");
        visitors.add(v1);
        visitors.add(v2);
        assertThrows(PhoneDuplicatedException.class, () -> reserveService.checkDuplicatedPhone(visitors));
        visitors.clear();

        VisitorDto v3 = new VisitorDto("?????????3", "01099999999", "42");
        VisitorDto v4 = new VisitorDto("?????????4", "01088888888", "42");
        visitors.add(v3);
        visitors.add(v4);
        assertDoesNotThrow(() -> reserveService.checkDuplicatedPhone(visitors));


    }

    @Test
    @Transactional
    void ????????????() {
        List<VisitorModifyDto> visitors = new ArrayList<>();
        visitors.add(new VisitorModifyDto());
        String place = "??????";
        long reserveId = savedReserve.getId();
        ReserveModifyDto reserveModifyDto= new ReserveModifyDto(reserveId,
            place, seed.encrypt("jaehchoi"), "???????????????", LocalDateTime.now(), visitors);
        Reserve updateReserve = reserveService.updateReserve(reserveModifyDto, savedStaff.getId());
        em.flush();
        em.clear();
        assertEquals(updateReserve.getPlace(), reserveRepository.findById(reserveId).get().getPlace());
    }

    @Test
    @Transactional
    void ?????????_??????_???????????????_????????????() {
        ReserveRequestDto reserveRequestDto = new ReserveRequestDto("01011112222", "????????????");
        List<ReserveListResponseDto> reservesByNameAndPhone = reserveService.findReservesByNameAndPhone(reserveRequestDto);
        if (reservesByNameAndPhone.isEmpty()) {
            fail("???????????? ??????");
        }
        assertEquals(reservesByNameAndPhone.get(0).getId(), savedReserve.getId());
    }

    @Test
    @Transactional
    void ????????????() {
        Reserve temp = Reserve
            .builder()
            .targetStaff(savedStaff.getId())
            .purpose("??????")
            .date(LocalDateTime.now())
            .place("??????")
            .build();
        Reserve saved = reserveRepository.save(temp);
        long savedId = saved.getId();

        em.flush();
        Response response = reserveService.deleteById(savedId);
        assertEquals(response.getCode(), "2000");
        em.flush();
        Response failResponse = reserveService.deleteById(savedId);
        assertEquals(failResponse.getCode(), "4000");
    }

    @Test
    @Transactional
    void ????????????() {
        List<VisitorDto> mockVisitors = new ArrayList<>();
        mockVisitors.add(new VisitorDto());
        ReserveVisitorDto temp = new ReserveVisitorDto("??????", "?????????", "?????????", LocalDateTime.now(), mockVisitors);
        Reserve result = reserveService.saveReserve(temp, savedStaff.getId());
        assertNotNull(result);
        reserveRepository.delete(result);
    }

    @Test
    @Transactional
    void ?????????????????????_????????????() {

        Staff staff = Staff
            .builder()
            .name(seed.encrypt(seed.encrypt("abcde")))
            .phone(seed.encrypt(seed.encrypt("01012345678")))
            .department("????????????")
            .build();
        Staff staffData = staffRepository.save(staff);

        Reserve temp = Reserve
            .builder()
            .targetStaff(staffData.getId())
            .purpose("??????")
            .date(LocalDateTime.now())
            .place("??????")
            .build();
        Reserve reserveData = reserveRepository.save(temp);

        reserveService.deleteAllByStaffId(staffData.getId());

        em.flush();

        Optional<Reserve> result = reserveRepository.findById(reserveData.getId());

        assertThrows(NoSuchElementException.class, ()-> result.get());
    }

    @Test
    @Transactional
    void ???????????????????????????() {
        Reserve reserve = Reserve
            .builder()
            .targetStaff(savedStaff.getId())
            .purpose("??????")
            .date(LocalDateTime.now())
            .place("??????")
            .build();
        Reserve r1 = reserveRepository.save(reserve);

        String deleteVisitorName = "????????????";
        String deleteVisitorPhone = "01055555555";

        Visitor visitor = Visitor
            .builder()
            .name(seed.encrypt(deleteVisitorName))
            .organization("???????????????????????????")
            .reserve_id(r1.getId())
            .phone(seed.encrypt(deleteVisitorPhone))
            .build();
        Visitor v1 = visitorRepository.save(visitor);

        ReserveRequestDto reserveRequestDto= new ReserveRequestDto(deleteVisitorPhone, deleteVisitorName);
        reserveService.visitorReserveDelete(r1.getId(), reserveRequestDto);

        Optional<Reserve> r2 = reserveRepository.findById(r1.getId());
        Optional<Visitor> v2 = visitorRepository.findById(v1.getId());

        assertThrows(NoSuchElementException.class, () -> r2.get());
        assertThrows(NoSuchElementException.class, () -> v2.get());
    }

}