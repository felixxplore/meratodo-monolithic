package com.felix.meratodo.service;

import com.felix.meratodo.dto.ProjectRequestDto;
import com.felix.meratodo.dto.ProjectResponseDto;
import com.felix.meratodo.dto.TaskResponseDto;
import com.felix.meratodo.exception.ProjectNotFoundException;
import com.felix.meratodo.exception.TeamNotFoundException;
import com.felix.meratodo.mapper.ProjectMapper;
import com.felix.meratodo.mapper.TaskMapper;
import com.felix.meratodo.model.Project;
import com.felix.meratodo.model.Task;
import com.felix.meratodo.model.Team;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.ProjectRepository;
import com.felix.meratodo.repository.TaskRepository;
import com.felix.meratodo.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectMapper projectMapper;
    public ProjectResponseDto createProject(ProjectRequestDto dto) {
        return   projectMapper.toDto(projectRepository.save(projectMapper.toEntity(dto)));
    }


    public  ProjectResponseDto updateProjectById(Long id, ProjectRequestDto dto) {

        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found."));
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setDeadline(dto.getDeadline());
        project.setPublic(dto.isPublic());

        projectRepository.save(project);

        return projectMapper.toDto(project);
    }

    public void deleteProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found."));
        projectRepository.deleteById(id);
    }

    public List<ProjectResponseDto> getAllProjects() {
        List<Project> projects = projectRepository.findAll();

        return projectMapper.toDto(projects);

    }

    public ProjectResponseDto getProjectById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found."));
        return projectMapper.toDto(project);
    }

    public void assignProjectToTeam(Long projectId, Long teamId) {
         Project project=projectMapper.toEntity(getProjectById(projectId));
        Team team=teamRepository.findById(teamId).orElseThrow(()-> new TeamNotFoundException("Team not found."));

         project.setTeam(team);
         projectRepository.save(project);
    }

    public void removeTeamFromProject(Long id) {
        Project project=projectMapper.toEntity(getProjectById(id));

        Team team = project.getTeam();
        if(team==null){
            throw new RuntimeException("Project not assigned to team so you can't remove project from team.");
        }

        project.setTeam(null);
        projectRepository.save(project);
    }

    public void moveProjectToArchive(Long id) {
        Project project=projectMapper.toEntity(getProjectById(id));

        if(project.isArchived()){
            throw new RuntimeException("Project is already in archived");
        }

        project.setArchived(true);
        projectRepository.save(project);
    }

    public List<ProjectResponseDto> getMyProjects() {
        User currentUser = UserService.getCurrentUser();
       List<Project> projects= projectRepository.findAllByOwnerId(currentUser.getId());

       return projectMapper.toDto(projects);
    }

    public List<TaskResponseDto> getProjectTasks(Long id) {
        Project project=projectMapper.toEntity(getProjectById(id));

        List<Task> tasks = taskRepository.findByProjectId(id);

        return taskMapper.toDtoList(tasks);
    }
}
