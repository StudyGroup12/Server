import { FormEvent, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useCreateTodo, useTodoDetail, useUpdateTodo } from '../../hooks/useTodos';
import './Todo.css';

const TodoFormPage = () => {
  const { groupId, todoId } = useParams();
  const numericGroupId = Number(groupId);
  const numericTodoId = todoId ? Number(todoId) : undefined;
  const isEdit = numericTodoId !== undefined;
  const hasToken = !!sessionStorage.getItem('accessToken');
  const navigate = useNavigate();

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [dueDate, setDueDate] = useState('');

  const detailQuery = useTodoDetail(numericGroupId, numericTodoId);
  const createMutation = useCreateTodo(numericGroupId);
  const updateMutation = useUpdateTodo(numericGroupId, numericTodoId ?? 0);

  useEffect(() => {
    if (detailQuery.data?.data) {
      setTitle(detailQuery.data.data.title);
      setDescription(detailQuery.data.data.description ?? '');
      setDueDate(detailQuery.data.data.dueDate ?? '');
    }
  }, [detailQuery.data]);

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!title.trim()) {
      alert('할일 제목을 입력해주세요.');
      return;
    }

    const payload = { title, description, dueDate };
    const mutation = isEdit ? updateMutation : createMutation;
    mutation.mutate(payload, {
      onSuccess: () => navigate(`/groups/${numericGroupId}/todos`),
      onError: (error: any) => {
        alert(error.response?.data?.error?.message || '할일 저장에 실패했습니다.');
      },
    });
  };

  if (!hasToken) {
    return (
      <div className="empty-state">
        로그인 후 할일을 작성할 수 있습니다.
        <div className="group-form-actions">
          <Link to="/login" className="button button-primary">
            로그인
          </Link>
        </div>
      </div>
    );
  }

  if (isEdit && detailQuery.isError) {
    return (
      <div className="error-state">
        {(detailQuery.error as { response?: { data?: { error?: { message?: string } } } })
          ?.response?.data?.error?.message ?? '할일 정보를 불러오지 못했습니다.'}
      </div>
    );
  }

  return (
    <section className="todo-page todo-page--narrow">
      <div className="todo-toolbar">
        <div className="todo-title-block">
          <h1>{isEdit ? '할일 수정' : '새 할일'}</h1>
          <p>{isEdit ? '공동 할일 내용을 다듬어보세요.' : '그룹원들과 함께할 할일을 등록하세요.'}</p>
        </div>
      </div>

      <form className="todo-form" onSubmit={handleSubmit}>
        <label className="todo-form-field">
          <span>제목</span>
          <input
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            placeholder="예: 알고리즘 문제 3개 풀기"
            maxLength={200}
          />
        </label>
        <label className="todo-form-field">
          <span>설명</span>
          <textarea
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            placeholder="필요한 메모를 남겨두세요"
            rows={8}
            maxLength={2000}
          />
        </label>
        <label className="todo-form-field">
          <span>마감일</span>
          <input
            type="date"
            value={dueDate}
            onChange={(event) => setDueDate(event.target.value)}
          />
        </label>
        <div className="todo-form-actions">
          <Link to={`/groups/${numericGroupId}/todos`} className="button">
            취소
          </Link>
          <button
            type="submit"
            className="button button-primary"
            disabled={createMutation.isPending || updateMutation.isPending}
          >
            {isEdit ? '수정' : '등록'}
          </button>
        </div>
      </form>
    </section>
  );
};

export default TodoFormPage;
