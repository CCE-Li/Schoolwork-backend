package cn.uptra.schoolwork.modules.book.service;

import cn.uptra.schoolwork.common.result.PageResult;
import cn.uptra.schoolwork.modules.book.entity.Book;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BookService extends IService<Book> {
    public List<Book> getBookByBid(Integer bid);
    public List<Book> searchBooksByKeyword(String keyword);
    public List<Book> listBooks(String author, String title, String tags);
    public PageResult<Book> getBooks(Integer page, Integer pageSize, 
                                      String title, String author, String tags);
}
