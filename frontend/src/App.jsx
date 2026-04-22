import { useEffect, useMemo, useState } from "react";
import { api } from "./api";
import AppHeader from "./components/AppHeader";
import ProjectDetailView from "./components/ProjectDetailView";
import ProjectListView from "./components/ProjectListView";
import StatusBanners from "./components/StatusBanners";
import {
  EMPTY_COMMENT,
  EMPTY_LABEL,
  EMPTY_MEETING,
  EMPTY_PROJECT,
  EMPTY_TASK,
  MEETINGS_PER_PAGE,
  PROJECT_STATUSES,
} from "./constants/appConstants";
import { formatDate, sortMeetingsByDate, toArray } from "./utils/formatters";

function App() {
  const [projects, setProjects] = useState([]);
  const [openedProjectId, setOpenedProjectId] = useState(null);
  const [selectedTaskId, setSelectedTaskId] = useState(null);
  const [allLabels, setAllLabels] = useState([]);
  const [selectedExistingLabelId, setSelectedExistingLabelId] = useState("");
  const [meetingPage, setMeetingPage] = useState(0);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [projectSearch, setProjectSearch] = useState("");
  const [projectStatusFilter, setProjectStatusFilter] = useState("");

  const [projectForm, setProjectForm] = useState(EMPTY_PROJECT);
  const [taskForm, setTaskForm] = useState(EMPTY_TASK);
  const [meetingForm, setMeetingForm] = useState(EMPTY_MEETING);
  const [labelForm, setLabelForm] = useState(EMPTY_LABEL);
  const [commentForm, setCommentForm] = useState(EMPTY_COMMENT);

  const [editingProjectId, setEditingProjectId] = useState(null);
  const [editingTaskId, setEditingTaskId] = useState(null);
  const [editingMeetingId, setEditingMeetingId] = useState(null);
  const [editingLabelId, setEditingLabelId] = useState(null);
  const [editingCommentId, setEditingCommentId] = useState(null);

  const openedProject = useMemo(
    () => projects.find((project) => project.id === openedProjectId) || null,
    [projects, openedProjectId]
  );

  const selectedTask = useMemo(
    () => toArray(openedProject?.tasks).find((task) => task.id === selectedTaskId) || null,
    [openedProject, selectedTaskId]
  );

  const filteredProjects = useMemo(() => {
    return projects.filter((project) => {
      const text = `${project.title} ${project.description}`.toLowerCase();
      const byText = text.includes(projectSearch.toLowerCase());
      const byStatus = !projectStatusFilter || project.status === projectStatusFilter;
      return byText && byStatus;
    });
  }, [projects, projectSearch, projectStatusFilter]);

  const sortedMeetings = useMemo(() => sortMeetingsByDate(openedProject?.meetings || []), [openedProject]);
  const totalMeetingPages = useMemo(
    () => Math.max(1, Math.ceil(sortedMeetings.length / MEETINGS_PER_PAGE)),
    [sortedMeetings.length]
  );
  const pagedMeetings = useMemo(() => {
    const start = meetingPage * MEETINGS_PER_PAGE;
    return sortedMeetings.slice(start, start + MEETINGS_PER_PAGE);
  }, [meetingPage, sortedMeetings]);

  const projectTasks = useMemo(() => toArray(openedProject?.tasks), [openedProject]);
  const attachableLabels = useMemo(() => {
    if (!selectedTask) return [];
    const usedIds = new Set(toArray(selectedTask.labels).map((label) => label.id));
    return toArray(allLabels).filter((label) => !usedIds.has(label.id));
  }, [allLabels, selectedTask]);

  function notifySuccess(message) {
    setSuccess(message);
    setError("");
  }

  function notifyError(err) {
    setSuccess("");
    setError(err?.message || "Unknown error");
  }

  async function loadProjects({ keepOpenProjectId = openedProjectId, keepTaskId = selectedTaskId } = {}) {
    try {
      const data = projectStatusFilter
        ? await api.getProjectsByStatus(projectStatusFilter)
        : await api.getProjects();

      const list = data || [];
      setProjects(list);

      if (!list.length) {
        setOpenedProjectId(null);
        setSelectedTaskId(null);
        return;
      }

      if (!keepOpenProjectId) {
        return;
      }

      const stillExists = list.find((project) => project.id === keepOpenProjectId);
      if (!stillExists) {
        setOpenedProjectId(null);
        setSelectedTaskId(null);
        return;
      }

      setOpenedProjectId(stillExists.id);

      const tasks = toArray(stillExists.tasks);
      if (!tasks.length) {
        setSelectedTaskId(null);
        return;
      }

      const existingTask = tasks.find((task) => task.id === keepTaskId) || tasks[0];
      setSelectedTaskId(existingTask.id);
    } catch (err) {
      notifyError(err);
    }
  }

  async function loadLabels() {
    try {
      const data = await api.getLabels();
      setAllLabels(toArray(data));
    } catch (err) {
      notifyError(err);
    }
  }

  useEffect(() => {
    setLoading(true);
    Promise.all([loadProjects(), loadLabels()]).finally(() => setLoading(false));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    setSelectedExistingLabelId("");
  }, [selectedTaskId]);

  useEffect(() => {
    setMeetingPage((prev) => Math.min(prev, Math.max(0, totalMeetingPages - 1)));
  }, [totalMeetingPages]);

  async function submitProject(event) {
    event.preventDefault();
    try {
      if (editingProjectId) {
        await api.updateProject(editingProjectId, projectForm);
        notifySuccess("Project updated");
      } else {
        await api.createProject(projectForm);
        notifySuccess("Project created");
      }
      setProjectForm(EMPTY_PROJECT);
      setEditingProjectId(null);
      await loadProjects({ keepOpenProjectId: openedProjectId, keepTaskId: selectedTaskId });
    } catch (err) {
      notifyError(err);
    }
  }

  async function submitTask(event) {
    event.preventDefault();
    if (!openedProjectId) return;

    try {
      if (editingTaskId) {
        await api.updateTask(editingTaskId, taskForm);
        notifySuccess("Task updated");
      } else {
        await api.createTask(openedProjectId, taskForm);
        notifySuccess("Task created");
      }
      setTaskForm(EMPTY_TASK);
      setEditingTaskId(null);
      await loadProjects({ keepOpenProjectId: openedProjectId, keepTaskId: selectedTaskId });
    } catch (err) {
      notifyError(err);
    }
  }

  async function submitMeeting(event) {
    event.preventDefault();
    if (!openedProjectId) return;

    try {
      const payload = {
        title: meetingForm.title,
        meetingDate: meetingForm.meetingDate,
        description: meetingForm.description,
      };

      if (editingMeetingId) {
        await api.updateMeeting(editingMeetingId, payload);
        notifySuccess("Meeting updated");
      } else {
        await api.createMeeting(openedProjectId, payload);
        notifySuccess("Meeting created");
      }
      setMeetingForm(EMPTY_MEETING);
      setEditingMeetingId(null);
      await loadProjects({ keepOpenProjectId: openedProjectId, keepTaskId: selectedTaskId });
    } catch (err) {
      notifyError(err);
    }
  }

  async function submitLabel(event) {
    event.preventDefault();
    if (!selectedTaskId) return;

    try {
      if (editingLabelId) {
        await api.updateLabel(editingLabelId, labelForm);
        notifySuccess("Label updated");
      } else {
        await api.createLabel(selectedTaskId, labelForm);
        notifySuccess("Label linked to task");
      }
      setLabelForm(EMPTY_LABEL);
      setEditingLabelId(null);
      await loadLabels();
      await loadProjects({ keepOpenProjectId: openedProjectId, keepTaskId: selectedTaskId });
    } catch (err) {
      notifyError(err);
    }
  }

  async function attachExistingLabel() {
    if (!selectedTaskId || !selectedExistingLabelId) return;

    const selectedLabel = toArray(allLabels).find(
      (label) => String(label.id) === String(selectedExistingLabelId)
    );

    if (!selectedLabel) {
      notifyError(new Error("Selected label not found"));
      return;
    }

    try {
      await api.attachLabelToTask(selectedExistingLabelId, selectedTaskId);
      notifySuccess("Existing label attached");
      setSelectedExistingLabelId("");
      await loadLabels();
      await loadProjects({ keepOpenProjectId: openedProjectId, keepTaskId: selectedTaskId });
    } catch (err) {
      const message = (err?.message || "").toLowerCase();
      const routeMissing = message.includes("no static resource") || message.includes("404");

      if (!routeMissing) {
        notifyError(err);
        return;
      }

      try {
        await api.createLabel(selectedTaskId, { title: selectedLabel.title });
        notifySuccess("Existing label attached");
        setSelectedExistingLabelId("");
        await loadLabels();
        await loadProjects({ keepOpenProjectId: openedProjectId, keepTaskId: selectedTaskId });
      } catch (fallbackErr) {
        notifyError(fallbackErr);
      }
    }
  }

  async function submitComment(event) {
    event.preventDefault();
    if (!selectedTaskId) return;

    try {
      if (editingCommentId) {
        await api.updateComment(editingCommentId, commentForm);
        notifySuccess("Comment updated");
      } else {
        await api.createComment(selectedTaskId, commentForm);
        notifySuccess("Comment created");
      }
      setCommentForm(EMPTY_COMMENT);
      setEditingCommentId(null);
      await loadProjects({ keepOpenProjectId: openedProjectId, keepTaskId: selectedTaskId });
    } catch (err) {
      notifyError(err);
    }
  }

  async function removeProject(projectId) {
    try {
      await api.deleteProject(projectId);
      notifySuccess("Project deleted");
      setOpenedProjectId(null);
      setSelectedTaskId(null);
      await loadProjects({ keepOpenProjectId: null, keepTaskId: null });
    } catch (err) {
      notifyError(err);
    }
  }

  async function removeTask(taskId) {
    try {
      await api.deleteTask(taskId);
      notifySuccess("Task deleted");
      await loadProjects({ keepOpenProjectId: openedProjectId, keepTaskId: null });
    } catch (err) {
      notifyError(err);
    }
  }

  async function removeMeeting(meetingId) {
    try {
      await api.deleteMeeting(meetingId);
      notifySuccess("Meeting deleted");
      await loadProjects({ keepOpenProjectId: openedProjectId, keepTaskId: selectedTaskId });
    } catch (err) {
      notifyError(err);
    }
  }

  async function removeLabel(labelId) {
    try {
      await api.deleteLabel(labelId);
      notifySuccess("Label deleted");
      await loadLabels();
      await loadProjects({ keepOpenProjectId: openedProjectId, keepTaskId: selectedTaskId });
    } catch (err) {
      notifyError(err);
    }
  }

  async function removeComment(commentId) {
    try {
      await api.deleteComment(commentId);
      notifySuccess("Comment deleted");
      await loadProjects({ keepOpenProjectId: openedProjectId, keepTaskId: selectedTaskId });
    } catch (err) {
      notifyError(err);
    }
  }

  function startEditProject(project) {
    setEditingProjectId(project.id);
    setProjectForm({
      title: project.title,
      description: project.description,
      startDate: project.startDate?.slice(0, 16) || "",
      deadline: project.deadline?.slice(0, 16) || "",
      status: project.status,
    });
  }

  function resetProjectForm() {
    setEditingProjectId(null);
    setProjectForm(EMPTY_PROJECT);
  }

  function openProject(project) {
    setOpenedProjectId(project.id);
    setSelectedTaskId(toArray(project.tasks)[0]?.id || null);
    setMeetingPage(0);
    setEditingTaskId(null);
    setEditingMeetingId(null);
    setEditingLabelId(null);
    setEditingCommentId(null);
    setSelectedExistingLabelId("");
    setTaskForm(EMPTY_TASK);
    setMeetingForm(EMPTY_MEETING);
    setLabelForm(EMPTY_LABEL);
    setCommentForm(EMPTY_COMMENT);
  }

  function startEditTask(task) {
    setEditingTaskId(task.id);
    setTaskForm({ title: task.title, description: task.description });
  }

  function resetTaskForm() {
    setEditingTaskId(null);
    setTaskForm(EMPTY_TASK);
  }

  function startEditMeeting(meeting) {
    setEditingMeetingId(meeting.id);
    setMeetingForm({
      title: meeting.title,
      meetingDate: meeting.meetingDate?.slice(0, 16) || "",
      description: meeting.description || "",
    });
  }

  function resetMeetingForm() {
    setEditingMeetingId(null);
    setMeetingForm(EMPTY_MEETING);
  }

  function startEditLabel(label) {
    setEditingLabelId(label.id);
    setLabelForm({ title: label.title });
  }

  function resetLabelForm() {
    setEditingLabelId(null);
    setLabelForm(EMPTY_LABEL);
  }

  function startEditComment(comment) {
    setEditingCommentId(comment.id);
    setCommentForm({ content: comment.content });
  }

  function resetCommentForm() {
    setEditingCommentId(null);
    setCommentForm(EMPTY_COMMENT);
  }

  return (
    <div className="app">
      <AppHeader />
      <StatusBanners success={success} error={error} />

      {!openedProject && (
        <ProjectListView
          filteredProjects={filteredProjects}
          projectSearch={projectSearch}
          onProjectSearchChange={setProjectSearch}
          projectStatusFilter={projectStatusFilter}
          onProjectStatusFilterChange={setProjectStatusFilter}
          projectStatuses={PROJECT_STATUSES}
          onRefresh={() => loadProjects({ keepOpenProjectId: null, keepTaskId: null })}
          onOpenProject={openProject}
          onStartEditProject={startEditProject}
          onRemoveProject={removeProject}
          editingProjectId={editingProjectId}
          projectForm={projectForm}
          onProjectFormChange={(field, value) => setProjectForm((prev) => ({ ...prev, [field]: value }))}
          onSubmitProject={submitProject}
          onResetProjectForm={resetProjectForm}
          formatDate={formatDate}
          toArray={toArray}
        />
      )}

      {openedProject && (
        <ProjectDetailView
          openedProject={openedProject}
          onBackToProjects={() => setOpenedProjectId(null)}
          onStartEditOpenedProject={() => {
            startEditProject(openedProject);
            setOpenedProjectId(null);
          }}
          onRemoveProject={removeProject}
          formatDate={formatDate}
          submitTask={submitTask}
          taskForm={taskForm}
          onTaskFormChange={(field, value) => setTaskForm((prev) => ({ ...prev, [field]: value }))}
          editingTaskId={editingTaskId}
          onResetTaskForm={resetTaskForm}
          projectTasks={projectTasks}
          selectedTaskId={selectedTaskId}
          onSelectTask={setSelectedTaskId}
          onStartEditTask={startEditTask}
          onRemoveTask={removeTask}
          toArray={toArray}
          submitMeeting={submitMeeting}
          meetingForm={meetingForm}
          onMeetingFormChange={(field, value) => setMeetingForm((prev) => ({ ...prev, [field]: value }))}
          editingMeetingId={editingMeetingId}
          onResetMeetingForm={resetMeetingForm}
          pagedMeetings={pagedMeetings}
          sortedMeetings={sortedMeetings}
          onStartEditMeeting={startEditMeeting}
          onRemoveMeeting={removeMeeting}
          meetingPage={meetingPage}
          totalMeetingPages={totalMeetingPages}
          onPrevMeetingPage={() => setMeetingPage((prev) => Math.max(0, prev - 1))}
          onNextMeetingPage={() => setMeetingPage((prev) => Math.min(totalMeetingPages - 1, prev + 1))}
          selectedTask={selectedTask}
          selectedExistingLabelId={selectedExistingLabelId}
          onExistingLabelChange={setSelectedExistingLabelId}
          attachableLabels={attachableLabels}
          onAttachExistingLabel={attachExistingLabel}
          submitLabel={submitLabel}
          labelForm={labelForm}
          onLabelFormChange={(title) => setLabelForm({ title })}
          editingLabelId={editingLabelId}
          onResetLabelForm={resetLabelForm}
          onStartEditLabel={startEditLabel}
          onRemoveLabel={removeLabel}
          submitComment={submitComment}
          commentForm={commentForm}
          onCommentFormChange={(content) => setCommentForm({ content })}
          editingCommentId={editingCommentId}
          onResetCommentForm={resetCommentForm}
          onStartEditComment={startEditComment}
          onRemoveComment={removeComment}
        />
      )}

      <div className="small-note">{loading ? "Loading..." : "Synced with API"}</div>
    </div>
  );
}

export default App;
