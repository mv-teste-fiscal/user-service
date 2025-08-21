package com.teste_mv.user_service.Feign;


import com.teste_mv.user_service.DTO.TaskDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@FeignClient(name = "task-management-service", url = "${task.host}")
public interface TaskClient {

    @GetMapping("/task/userId/{userId}")
    List<TaskDTO> getTasksByUserId(@PathVariable("userId") Long id);
}
