package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {
        Book bookForm = new Book();
        bookForm.setName(form.getName());
        bookForm.setPrice(form.getPrice());
        bookForm.setStockQuantity(form.getStockQuantity());
        bookForm.setAuthor(form.getAuthor());
        bookForm.setIsbn(form.getIsbn());

        itemService.saveItem(bookForm);

        return "redirect:/";
    }
}
