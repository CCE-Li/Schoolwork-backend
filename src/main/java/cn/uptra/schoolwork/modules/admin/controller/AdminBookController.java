package cn.uptra.schoolwork.modules.admin.controller;

import cn.uptra.schoolwork.common.result.R;
import cn.uptra.schoolwork.modules.book.entity.Book;
import cn.uptra.schoolwork.modules.book.service.BookService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin/books")
@Tag(name = "Admin - 图书管理")
@RequiredArgsConstructor
public class AdminBookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "添加新图书")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Book> addBook(@RequestBody Book book) {
        bookService.save(book);
        return R.success(book);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新图书信息")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Book> updateBook(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        bookService.updateById(book);
        return R.success(book);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除图书")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> deleteBook(@PathVariable Long id) {
        bookService.removeById(id);
        return R.success();
    }

    @PutMapping("/{id}/status/{status}")
    @Operation(summary = "更新图书状态（上下架）")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> updateBookStatus(@PathVariable Long id, @PathVariable Integer status) {
        Book book = new Book();
        book.setId(id);
        book.setStatus(status);
        bookService.updateById(book);
        return R.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取图书详情")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Book> getBookById(@PathVariable Long id) {
        return R.success(bookService.getById(id));
    }

    @GetMapping
    @Operation(summary = "分页查询图书列表")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Page<Book>> listBooks(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        return R.success(bookService.page(page, null));
    }

    @PutMapping("/{id}/price")
    @Operation(summary = "修改图书价格")
    @PreAuthorize("hasRole('ADMIN')")
    public R<Void> updateBookPrice(@PathVariable Long id, @RequestParam BigDecimal price) {
        Book book = new Book();
        book.setId(id);
        book.setPrice(price);
        bookService.updateById(book);
        return R.success();
    }
}
