package cn.uptra.schoolwork.modules.book.service.impl;

import cn.uptra.schoolwork.common.result.PageResult;
import cn.uptra.schoolwork.modules.book.entity.Book;
import cn.uptra.schoolwork.modules.book.mapper.BookMapper;
import cn.uptra.schoolwork.modules.book.service.BookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
    /**
     * 通过bid获取书籍
     * @param bid
     * @return
     */
    @Override
    public List<Book> getBookByBid(Long bid) {
        return this.baseMapper.getBookByBid(bid);
    }

    /**
     * 通过关键词模糊查询书籍
     * @param keyword
     * @return
     */
    @Override
    public List<Book> searchBooksByKeyword(String keyword) {
        return this.baseMapper.searchBooksByKeyword(keyword);
    }

    /**
     * 多条件查询书籍
     * @param author
     * @param title
     * @param tags
     * @return
     */
    @Override
    public List<Book> listBooks(String author, String title, String tags) {
        return this.baseMapper.listBooks(author, title, tags);
    }

    @Override
    public PageResult<Book> getBooks(Integer page, Integer pageSize, 
                                      String title, String author, String tags) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        
        // 处理tags参数
        List<String> tagList = null;
        if (tags != null && !tags.isEmpty()) {
            tagList = Arrays.asList(tags.split(","));
        }
        
        // 查询数据
        List<Book> list = baseMapper.selectBooks(offset, pageSize, title, author, tagList);
        
        // 查询总数
        int total = baseMapper.countBooks(title, author, tagList);
        
        return new PageResult<>(list, total);
    }

    @Override
    public List<Book> getBooksByIds(List<Long> bookIds) {
        return lambdaQuery().in(Book::getBid, bookIds).list();
    }

    @Override
    public Book getByBid(Long bid) {
        return this.baseMapper.getByBid(bid);
    }
}
