function ProjectListView({
  filteredProjects,
  projectSearch,
  onProjectSearchChange,
  projectStatusFilter,
  onProjectStatusFilterChange,
  projectStatuses,
  onRefresh,
  onOpenProject,
  onStartEditProject,
  onRemoveProject,
  editingProjectId,
  projectForm,
  onProjectFormChange,
  onSubmitProject,
  onResetProjectForm,
  formatDate,
  toArray,
}) {
  return (
    <section className="panel list-shell">
      <div className="list-toolbar">
        <input
          placeholder="Search projects"
          value={projectSearch}
          onChange={(event) => onProjectSearchChange(event.target.value)}
        />
        <select value={projectStatusFilter} onChange={(event) => onProjectStatusFilterChange(event.target.value)}>
          <option value="">All statuses</option>
          {projectStatuses.map((status) => (
            <option key={status} value={status}>
              {status}
            </option>
          ))}
        </select>
        <button className="secondary" onClick={onRefresh}>
          Refresh
        </button>
      </div>

      <div className="project-grid">
        {filteredProjects.map((project) => (
          <article key={project.id} className="project-tile">
            <div className="tile-top">
              <h3>{project.title}</h3>
              <span className="status-pill">{project.status}</span>
            </div>
            <p className="meta">{project.description}</p>
            <p className="meta">{toArray(project.tasks).length} tasks • {toArray(project.meetings).length} meetings</p>
            <p className="meta">{formatDate(project.startDate)} -&gt; {formatDate(project.deadline)}</p>
            <div className="btn-group">
              <button onClick={() => onOpenProject(project)}>Open project</button>
              <button className="secondary" onClick={() => onStartEditProject(project)}>
                Edit
              </button>
              <button className="danger" onClick={() => onRemoveProject(project.id)}>
                Delete
              </button>
            </div>
          </article>
        ))}
      </div>

      {!filteredProjects.length && <p className="small-note">No projects found.</p>}

      <form className="inline-form create-project" onSubmit={onSubmitProject}>
        <h3>{editingProjectId ? "Edit project" : "Create project"}</h3>
        <div className="row">
          <input
            placeholder="Title"
            value={projectForm.title}
            onChange={(event) => onProjectFormChange("title", event.target.value)}
            required
          />
          <select
            value={projectForm.status}
            onChange={(event) => onProjectFormChange("status", event.target.value)}
          >
            {projectStatuses.map((status) => (
              <option key={status} value={status}>
                {status}
              </option>
            ))}
          </select>
        </div>
        <textarea
          placeholder="Description"
          value={projectForm.description}
          onChange={(event) => onProjectFormChange("description", event.target.value)}
          required
        />
        <div className="row">
          <input
            type="datetime-local"
            value={projectForm.startDate}
            onChange={(event) => onProjectFormChange("startDate", event.target.value)}
            required
          />
          <input
            type="datetime-local"
            value={projectForm.deadline}
            onChange={(event) => onProjectFormChange("deadline", event.target.value)}
            required
          />
        </div>
        <div className="btn-group">
          <button type="submit">{editingProjectId ? "Save" : "Create"}</button>
          <button type="button" className="secondary" onClick={onResetProjectForm}>
            Reset
          </button>
        </div>
      </form>
    </section>
  );
}

export default ProjectListView;
