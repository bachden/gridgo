package io.gridgo.pojo.test.support;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionPojo {

    private List<Bar> listBar;
    
    private List<Bar>[] listBarArray;

    private List<Bar[]> listArrayBar;

    private Map<String, List<Bar>[]> map;

    private Set<int[]> setIntArray;

    @SuppressWarnings("rawtypes")
    private List[] rawListArray;

    private int[][] array2d;
}
