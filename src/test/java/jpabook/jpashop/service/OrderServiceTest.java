package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void 상품주문() {
        //given
        Member member = createMember();
        Book book = createBook();
        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order findOrder = orderRepository.findOne(orderId);

        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        assertThat(findOrder.getTotalPrice()).isEqualTo(orderCount * 1000);
        assertThat(findOrder.getOrderItems().size()).isEqualTo(1);
        assertThat(book.getStockQuantity()).isEqualTo(8);
    }

    @Test
    void 상품주문_재고수량초과() {
        //given
        Member member = createMember();
        Book book = createBook();
        int orderCount = 11;

        //when, then
        NotEnoughStockException e = assertThrows(NotEnoughStockException.class,
                ()-> orderService.order(member.getId(), book.getId(), orderCount));
        assertThat(e.getMessage()).isEqualTo("재고가 부족합니다");
    }

    @Test
    void 주문취소() {
        //given
        Member member = createMember();
        Book book = createBook();
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order findOrder = orderRepository.findOne(orderId);

        assertThat(book.getStockQuantity()).isEqualTo(10);
        assertThat(findOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
    }

    private @NonNull Book createBook() {
        Book book = new Book();
        book.setName("JPA 활용편");
        book.setAuthor("김영한");
        book.setIsbn("1234");
        book.setPrice(1000);
        book.setStockQuantity(10);
        em.persist(book);
        return book;
    }

    private @NonNull Member createMember() {
        Member member = new Member();
        member.setName("홍길동");
        member.setAddress(new Address("시티", "스트릿", "집코드"));
        em.persist(member);
        return member;
    }
}