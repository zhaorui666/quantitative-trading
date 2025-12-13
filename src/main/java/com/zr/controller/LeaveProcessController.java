package com.zr.controller;

import cn.hutool.json.JSONObject;
import com.zr.service.LeaveProcessService;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/process")
public class LeaveProcessController {

    @Autowired
    private LeaveProcessService processService;

    // 发起流程
    @PostMapping("/start")
    public ResponseEntity startProcess(
            @RequestParam String applicant,
            @RequestParam String manager,
            @RequestParam String reason,
            @RequestParam int days
    ) {
        String processId = processService.startProcess(applicant, reason, manager, days);
        return ResponseEntity.ok(new JSONObject().append("流程启动成功", processId));
    }

    // 处理任务
    @PostMapping("/complete")
    public ResponseEntity completeTask(
            @RequestParam String taskId,
            @RequestParam String assignee,
            @RequestParam boolean approved
    ) {
        processService.completeTask(taskId, assignee, approved);
        return ResponseEntity.ok("任务处理完成");
    }

    // 查询待办任务
    @GetMapping("/tasks")
    public ResponseEntity getTasks(@RequestParam String assignee) {
        List<Task> tasks = processService.getTasks(assignee);
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("获取任务成功", tasks);
        return ResponseEntity.ok(jsonObject);
    }
}
