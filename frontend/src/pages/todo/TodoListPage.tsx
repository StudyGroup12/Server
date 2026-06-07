import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link, useParams } from 'react-router-dom';
import { fetchGroupDetail } from '../../api/group.api';
import { useAuth } from '../../hooks/useAuth';
import {
  useDeleteTodo,
  useTodoList,
  useTodoProgress,
  useUpdateTodoComplete,
} from '../../hooks/useTodos';
import './Todo.css';

type TodoFilter = 'all' | 'active' | 'completed';

const toCompletedParam = (filter: TodoFilter): boolean | undefined => {
  if (filter === 'active') return false;
  if (filter === 'completed') return true;
  return undefined;
};

const TodoListPage = () => {
  const { groupId } = useParams();
  const numericGroupId = Number(groupId);
  const { user } = useAuth();
  const hasToken = !!sessionStorage.getItem('accessToken');
  const [page, setPage] = useState(0);
  const [filter, setFilter] = useState<TodoFilter>('all');

  const completed = toCompletedParam(filter);
  const todoQuery = useTodoList(numericGroupId, page, completed);
  const progressQuery = useTodoProgress(numericGroupId);
  const groupQuery = useQuery({
    queryKey: ['group', numericGroupId],
    queryFn: () => fetchGroupDetail(numericGroupId),
    enabled: hasToken && Number.isFinite(numericGroupId),
  });
  const completeMutation = useUpdateTodoComplete(numericGroupId);
  const deleteMutation = useDeleteTodo(numericGroupId);

  const todos = todoQuery.data?.data.content ?? [];
  const pageData = todoQuery.data?.data;
  const progress = progressQuery.data?.data ?? {
    totalCount: 0,
    completedCount: 0,
    progressRate: 0,
  };
  const remainingCount = Math.max(progress.totalCount - progress.completedCount, 0);

  const handleFilterChange = (nextFilter: TodoFilter) => {
    setFilter(nextFilter);
    setPage(0);
  };

  const handleToggle = (todoId: number, nextCompleted: boolean) => {
    completeMutation.mutate(
      { todoId, completed: nextCompleted },
      {
        onError: (error: any) => {
          alert(error.response?.data?.error?.message || '완료 상태 변경에 실패했습니다.');
        },
      }
    );
  };

  const handleDelete = (todoId: number) => {
    if (!window.confirm('이 할일을 삭제할까요?')) {
      return;
    }

    deleteMutation.mutate(todoId, {
      onError: (error: any) => {
        alert(error.response?.data?.error?.message || '할일 삭제에 실패했습니다.');
      },
    });
  };

  if (!hasToken) {
    return (
      <div className="empty-state">
        로그인 후 할일을 이용할 수 있습니다.
        <div className="group-form-actions">
          <Link to="/login" className="button button-primary">
            로그인
          </Link>
        </div>
      </div>
    );
  }

  return (
    <section className="todo-page">
      <div className="todo-toolbar">
        <div className="todo-title-block">
          <h1>공동 할일</h1>
          <p>그룹원들과 함께 진행할 할일을 관리하세요.</p>
        </div>
        <div className="todo-toolbar-actions">
          <Link to={`/groups/${numericGroupId}`} className="button">
            그룹으로
          </Link>
          <Link to={`/groups/${numericGroupId}/todos/new`} className="button button-primary">
            할일 추가
          </Link>
        </div>
      </div>

      <div className="todo-progress-card">
        <div>
          <span className="todo-progress-label">그룹 진행률</span>
          <strong>{progress.progressRate}%</strong>
          <p>
            완료 {progress.completedCount}개 · 남은 할일 {remainingCount}개
          </p>
        </div>
        <div className="todo-progress-track" aria-label={`진행률 ${progress.progressRate}%`}>
          <span style={{ width: `${progress.progressRate}%` }} />
        </div>
      </div>

      <div className="todo-filter-row">
        <div className="todo-filter-toggle">
          <button
            type="button"
            className={filter === 'all' ? 'active' : ''}
            onClick={() => handleFilterChange('all')}
          >
            전체
          </button>
          <button
            type="button"
            className={filter === 'active' ? 'active' : ''}
            onClick={() => handleFilterChange('active')}
          >
            미완료
          </button>
          <button
            type="button"
            className={filter === 'completed' ? 'active' : ''}
            onClick={() => handleFilterChange('completed')}
          >
            완료
          </button>
        </div>
      </div>

      {todoQuery.isLoading && <div className="loading-state">할일을 불러오는 중입니다.</div>}
      {todoQuery.isError && (
        <div className="error-state">
          {(todoQuery.error as { response?: { data?: { error?: { message?: string } } } })
            ?.response?.data?.error?.message ?? '할일 목록을 불러오지 못했습니다.'}
        </div>
      )}

      {!todoQuery.isLoading && !todoQuery.isError && todos.length === 0 && (
        <div className="empty-state">아직 등록된 할일이 없습니다.</div>
      )}

      {!todoQuery.isLoading && !todoQuery.isError && todos.length > 0 && (
        <ul className="todo-list">
          {todos.map((todo) => (
            <li key={todo.id} className={`todo-row${todo.completed ? ' is-completed' : ''}`}>
              <label className="todo-check">
                <input
                  type="checkbox"
                  checked={todo.completed}
                  disabled={completeMutation.isPending}
                  onChange={(event) => handleToggle(todo.id, event.target.checked)}
                />
                <span />
              </label>
              <div className="todo-row-content">
                <h2>{todo.title}</h2>
                {todo.description && <p>{todo.description}</p>}
                <div className="todo-row-meta">
                  {todo.dueDate && <span>마감일 {new Date(todo.dueDate).toLocaleDateString()}</span>}
                  <span>{todo.completed ? '완료' : '진행 중'}</span>
                  <span>등록일 {new Date(todo.createdAt).toLocaleDateString()}</span>
                </div>
              </div>
              {user && (user.id === todo.memberId || user.id === groupQuery.data?.data.ownerId) && (
                <div className="todo-row-actions">
                  <Link to={`/groups/${numericGroupId}/todos/${todo.id}/edit`} className="button">
                    수정
                  </Link>
                  <button
                    type="button"
                    className="button button-danger"
                    onClick={() => handleDelete(todo.id)}
                    disabled={deleteMutation.isPending}
                  >
                    삭제
                  </button>
                </div>
              )}
            </li>
          ))}
        </ul>
      )}

      {pageData && pageData.totalPages > 1 && (
        <div className="pagination">
          <button
            type="button"
            className="button"
            onClick={() => setPage((current) => Math.max(current - 1, 0))}
            disabled={pageData.first}
          >
            이전
          </button>
          <span>
            {pageData.number + 1} / {pageData.totalPages}
          </span>
          <button
            type="button"
            className="button"
            onClick={() => setPage((current) => current + 1)}
            disabled={pageData.last}
          >
            다음
          </button>
        </div>
      )}
    </section>
  );
};

export default TodoListPage;
