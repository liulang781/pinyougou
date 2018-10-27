package page;
import java.io.Serializable;
import java.util.List;

/**
 * 分页结果pojo类
 */
public class PageResult implements Serializable {

    private Long total;//数据的总记录数
    private List rows;//当前页显示的数据

    public PageResult(Long total, List rows) {
        this.total = total;
        this.rows = rows;
}

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
