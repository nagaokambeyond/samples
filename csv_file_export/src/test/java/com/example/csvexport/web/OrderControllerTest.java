package com.example.csvexport.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.csvexport.dao.OrderDao;
import com.example.csvexport.model.OrderFilter;
import com.example.csvexport.service.ExportJobService;
import com.example.csvexport.storage.FileStorage;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

class OrderControllerTest {
    @Test
    void usesAnEmptyFilterWhenTheRequestHasNoSearchParameters() {
        OrderDao orderDao = org.mockito.Mockito.mock(OrderDao.class);
        ExportJobService exportJobService = org.mockito.Mockito.mock(ExportJobService.class);
        FileStorage fileStorage = org.mockito.Mockito.mock(FileStorage.class);
        when(orderDao.findPreview(any(OrderFilter.class))).thenReturn(List.of());
        when(exportJobService.ownedJobs("alice")).thenReturn(List.of());

        var controller = new OrderController(orderDao, exportJobService, fileStorage);
        var model = new ConcurrentModel();

        assertThat(controller.index(null, () -> "alice", model)).isEqualTo("index");
        assertThat(model.getAttribute("filter")).isEqualTo(OrderFilter.empty());
        verify(orderDao).findPreview(OrderFilter.empty());
    }
}
