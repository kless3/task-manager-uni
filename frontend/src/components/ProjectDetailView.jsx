function ProjectDetailView({
  openedProject,
  onBackToProjects,
  onStartEditOpenedProject,
  onRemoveProject,
  formatDate,
  submitTask,
  taskForm,
  onTaskFormChange,
  editingTaskId,
  onResetTaskForm,
  projectTasks,
  selectedTaskId,
  onSelectTask,
  onStartEditTask,
  onRemoveTask,
  toArray,
  submitMeeting,
  meetingForm,
  onMeetingFormChange,
  editingMeetingId,
  onResetMeetingForm,
  pagedMeetings,
  sortedMeetings,
  onStartEditMeeting,
  onRemoveMeeting,
  meetingPage,
  totalMeetingPages,
  onPrevMeetingPage,
  onNextMeetingPage,
  selectedTask,
  selectedExistingLabelId,
  onExistingLabelChange,
  attachableLabels,
  onAttachExistingLabel,
  submitLabel,
  labelForm,
  onLabelFormChange,
  editingLabelId,
  onResetLabelForm,
  onStartEditLabel,
  onRemoveLabel,
  submitComment,
  commentForm,
  onCommentFormChange,
  editingCommentId,
  onResetCommentForm,
  onStartEditComment,
  onRemoveComment,
}) {
  return (
    <section className="panel detail-shell">
      <div className="detail-top">
        <button className="secondary" onClick={onBackToProjects}>
          Back to projects
        </button>
        <div className="detail-title">
          <h2>{openedProject.title}</h2>
          <p className="meta">{openedProject.description}</p>
          <p className="meta">
            {openedProject.status} | {formatDate(openedProject.startDate)} -&gt; {formatDate(openedProject.deadline)}
          </p>
        </div>
        <div className="btn-group">
          <button className="secondary" onClick={onStartEditOpenedProject}>
            Edit project
          </button>
          <button className="danger" onClick={() => onRemoveProject(openedProject.id)}>
            Delete project
          </button>
        </div>
      </div>

      <div className="split split-main">
        <article className="subpanel subpanel-entity">
          <div className="subpanel-head">
            <h3>Tasks</h3>
          </div>

          <form className="inline-form" onSubmit={submitTask}>
            <input
              placeholder="Task title"
              value={taskForm.title}
              onChange={(event) => onTaskFormChange("title", event.target.value)}
              required
            />
            <textarea
              placeholder="Task description"
              value={taskForm.description}
              onChange={(event) => onTaskFormChange("description", event.target.value)}
              required
            />
            <div className="btn-group">
              <button type="submit">{editingTaskId ? "Save" : "Add"}</button>
              <button type="button" className="secondary" onClick={onResetTaskForm}>
                Reset
              </button>
            </div>
          </form>

          <div className="list compact entity-list">
            {projectTasks.map((task) => (
              <article
                key={task.id}
                className={`item selectable ${selectedTaskId === task.id ? "selected" : ""}`}
                onClick={() => onSelectTask(task.id)}
              >
                <div className="item-title">
                  <strong>{task.title}</strong>
                  <div className="btn-group">
                    <button
                      className="secondary"
                      onClick={(event) => {
                        event.stopPropagation();
                        onStartEditTask(task);
                      }}
                    >
                      Edit
                    </button>
                    <button
                      className="danger"
                      onClick={(event) => {
                        event.stopPropagation();
                        onRemoveTask(task.id);
                      }}
                    >
                      Delete
                    </button>
                  </div>
                </div>
                <p className="meta">{task.description}</p>
                <div className="tags">
                  {toArray(task.labels).map((label) => (
                    <span key={label.id} className="tag">
                      {label.title}
                    </span>
                  ))}
                </div>
              </article>
            ))}
            {!projectTasks.length && <p className="small-note">No tasks yet.</p>}
          </div>
        </article>

        <article className="subpanel subpanel-entity">
          <h3>Meetings</h3>
          <form className="inline-form" onSubmit={submitMeeting}>
            <input
              placeholder="Meeting title"
              value={meetingForm.title}
              onChange={(event) => onMeetingFormChange("title", event.target.value)}
              required
            />
            <input
              type="datetime-local"
              value={meetingForm.meetingDate}
              onChange={(event) => onMeetingFormChange("meetingDate", event.target.value)}
              required
            />
            <textarea
              placeholder="Meeting description"
              value={meetingForm.description}
              onChange={(event) => onMeetingFormChange("description", event.target.value)}
            />
            <div className="btn-group">
              <button type="submit">{editingMeetingId ? "Save" : "Add"}</button>
              <button type="button" className="secondary" onClick={onResetMeetingForm}>
                Reset
              </button>
            </div>
          </form>

          <div className="list compact entity-list">
            {pagedMeetings.map((meeting) => (
              <article key={meeting.id} className="item">
                <div className="item-title">
                  <strong>{meeting.title}</strong>
                  <div className="btn-group">
                    <button className="secondary" onClick={() => onStartEditMeeting(meeting)}>
                      Edit
                    </button>
                    <button className="danger" onClick={() => onRemoveMeeting(meeting.id)}>
                      Delete
                    </button>
                  </div>
                </div>
                <p className="meta">{formatDate(meeting.meetingDate)}</p>
                <p className="meta">{meeting.description || "No description"}</p>
              </article>
            ))}
            {!sortedMeetings.length && <p className="small-note">No meetings yet.</p>}
          </div>

          <div className="pager">
            <button type="button" className="secondary" onClick={onPrevMeetingPage} disabled={meetingPage === 0}>
              Prev
            </button>
            <span className="small-note pager-info">
              Page {meetingPage + 1} of {totalMeetingPages}
            </span>
            <button
              type="button"
              className="secondary"
              onClick={onNextMeetingPage}
              disabled={meetingPage >= totalMeetingPages - 1}
            >
              Next
            </button>
          </div>
        </article>
      </div>

      <article className="subpanel bottom">
        <h3>Task details</h3>
        {selectedTask ? (
          <>
            <p className="meta">Current task: {selectedTask.title}</p>
            <div className="split">
              <div>
                <h4>Labels</h4>
                <div className="row row-attach">
                  <select value={selectedExistingLabelId} onChange={(event) => onExistingLabelChange(event.target.value)}>
                    <option value="">Select existing label</option>
                    {attachableLabels.map((label) => (
                      <option key={label.id} value={label.id}>
                        {label.title}
                      </option>
                    ))}
                  </select>
                  <button type="button" className="secondary" onClick={onAttachExistingLabel} disabled={!selectedExistingLabelId}>
                    Attach
                  </button>
                </div>
                <form className="inline-form" onSubmit={submitLabel}>
                  <input
                    placeholder="Label title"
                    value={labelForm.title}
                    onChange={(event) => onLabelFormChange(event.target.value)}
                    required
                  />
                  <div className="btn-group">
                    <button type="submit">{editingLabelId ? "Save" : "Add"}</button>
                    <button type="button" className="secondary" onClick={onResetLabelForm}>
                      Reset
                    </button>
                  </div>
                </form>

                <div className="tags labels-wrap">
                  {toArray(selectedTask.labels).map((label) => (
                    <span key={label.id} className="tag tag-action">
                      {label.title}
                      <button className="tag-btn" onClick={() => onStartEditLabel(label)}>
                        edit
                      </button>
                      <button className="tag-btn danger-text" onClick={() => onRemoveLabel(label.id)}>
                        del
                      </button>
                    </span>
                  ))}
                </div>
              </div>

              <div>
                <h4>Comments</h4>
                <form className="inline-form" onSubmit={submitComment}>
                  <textarea
                    placeholder="Comment text"
                    value={commentForm.content}
                    onChange={(event) => onCommentFormChange(event.target.value)}
                    required
                  />
                  <div className="btn-group">
                    <button type="submit">{editingCommentId ? "Save" : "Add"}</button>
                    <button type="button" className="secondary" onClick={onResetCommentForm}>
                      Reset
                    </button>
                  </div>
                </form>

                <div className="list compact">
                  {toArray(selectedTask.comments).map((comment) => (
                    <article key={comment.id} className="item">
                      <div className="item-title">
                        <strong>#{comment.id}</strong>
                        <div className="btn-group">
                          <button className="secondary" onClick={() => onStartEditComment(comment)}>
                            Edit
                          </button>
                          <button className="danger" onClick={() => onRemoveComment(comment.id)}>
                            Delete
                          </button>
                        </div>
                      </div>
                      <p className="meta">{comment.content}</p>
                      <p className="meta">
                        {formatDate(comment.createdDate)} {comment.updatedDate ? `| upd ${formatDate(comment.updatedDate)}` : ""}
                      </p>
                    </article>
                  ))}
                  {!toArray(selectedTask.comments).length && <p className="small-note">No comments yet.</p>}
                </div>
              </div>
            </div>
          </>
        ) : (
          <p className="small-note">Select a task to see labels and comments.</p>
        )}
      </article>
    </section>
  );
}

export default ProjectDetailView;
