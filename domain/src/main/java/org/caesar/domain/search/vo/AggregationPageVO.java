package org.caesar.domain.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.caesar.domain.search.enums.DataSource;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregationPageVO<T> {
    private DataSource dataSource;
    private PageVO<T> pageVO;
}
