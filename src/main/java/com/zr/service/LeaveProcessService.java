package com.zr.service;

import liquibase.repackaged.org.apache.commons.collections4.map.HashedMap;
import org.flowable.engine.*;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

@Service
public class LeaveProcessService {

    private final ProcessEngine processEngine;

    public LeaveProcessService(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    // 1. 发起请假流程
    public String startProcess(String applicant, String reason, String manager, int days) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();

        HashedMap<String, Object> map = new HashedMap<>();
        map.put("applicant", applicant);
        map.put("reason", reason);
        map.put("days", days);
        map.put("manager", manager);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "leaveProcess",  // 流程定义Key
                "process_" + UUID.randomUUID(),  // businessKey
                map);

        Task t = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
        return t.getId();
    }


    // 2. 处理审批任务
    public void completeTask(String taskId, String assignee, boolean approved) {
        HashedMap<String, Object> map = new HashedMap<>();
        map.put("approved", approved);
        map.put("employee", assignee);


        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();
        taskService.claim(taskId, assignee);  // 认领任务
        taskService.complete(taskId, map);  // 提交审批结果

        //无论同意还是拒绝，均需要完成任务
        Task t = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        taskService.complete(t.getId());
    }


    // 3. 查询用户待办任务
    public List<Task> getTasks(String assignee) {
        return processEngine.getTaskService()
                .createTaskQuery()
                .taskAssignee(assignee)
                .orderByTaskCreateTime()
                .desc()
                .list();
    }
}
