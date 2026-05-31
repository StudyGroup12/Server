import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { deleteGroup, fetchGroupDetail } from '../../api/group.api';
import { useAuth } from '../../hooks/useAuth';
import './Group.css';

const GroupDetailPage = () => {
  const { groupId } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { user } = useAuth();
  const numericGroupId = Number(groupId);
  const hasToken = !!sessionStorage.getItem('accessToken');

  const { data, isLoading, isError } = useQuery({
    queryKey: ['group', numericGroupId],
    queryFn: () => fetchGroupDetail(numericGroupId),
    enabled: hasToken && Number.isFinite(numericGroupId),
  });

  const deleteMutation = useMutation({
    mutationFn: () => deleteGroup(numericGroupId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['groups'] });
      navigate('/groups');
    },
    onError: (error: any) => {
      alert(error.response?.data?.error?.message || '그룹 삭제에 실패했습니다.');
    },
  });

  const group = data?.data;
  const isOwner = !!user && !!group && user.id === group.ownerId;

  const handleDelete = () => {
    if (window.confirm('이 그룹을 삭제할까요?')) {
      deleteMutation.mutate();
    }
  };

  if (!hasToken) {
    return (
      <div className="empty-state">
        로그인 후 그룹 정보를 조회할 수 있습니다.
        <div className="group-form-actions">
          <Link to="/login" className="button button-primary">
            로그인
          </Link>
        </div>
      </div>
    );
  }

  if (isLoading) {
    return <div className="loading-state">그룹 정보를 불러오는 중입니다.</div>;
  }

  if (isError || !group) {
    return <div className="error-state">그룹 정보를 찾을 수 없습니다.</div>;
  }

  return (
    <section className="group-page">
      <div className="group-detail-header">
        <div>
          <span className="group-detail-category">{group.category}</span>
          <h1>{group.name}</h1>
          <p>{group.currentMemberCount}명이 참여 중입니다.</p>
        </div>
        <div className="group-actions">
          <Link to="/groups" className="button">
            목록
          </Link>
          {isOwner && (
            <>
              <Link to={`/groups/${group.id}/edit`} className="button button-primary">
                수정
              </Link>
              <button
                type="button"
                className="button button-danger"
                onClick={handleDelete}
                disabled={deleteMutation.isPending}
              >
                삭제
              </button>
            </>
          )}
        </div>
      </div>

      <article className="group-detail">
        <p className="group-detail-description">{group.description}</p>
        <div className="group-detail-meta">
          <div>
            <span className="meta-label">정원</span>
            <span className="meta-value">
              {group.currentMemberCount}/{group.maxMemberCount}명
            </span>
          </div>
          <div>
            <span className="meta-label">생성일</span>
            <span className="meta-value">{new Date(group.createdAt).toLocaleDateString()}</span>
          </div>
          <div>
            <span className="meta-label">수정일</span>
            <span className="meta-value">{new Date(group.updatedAt).toLocaleDateString()}</span>
          </div>
        </div>
      </article>
    </section>
  );
};

export default GroupDetailPage;
