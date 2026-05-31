import { FormEvent, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { fetchGroups } from '../../api/group.api';
import { useAuth } from '../../hooks/useAuth';
import './Group.css';

const PAGE_SIZE = 6;

const GroupListPage = () => {
  const { user } = useAuth();
  const [searchInput, setSearchInput] = useState('');
  const [keyword, setKeyword] = useState('');
  const [page, setPage] = useState(0);
  const hasToken = !!sessionStorage.getItem('accessToken');

  const { data, isLoading, isError } = useQuery({
    queryKey: ['groups', keyword, page],
    queryFn: () => fetchGroups({ keyword, page, size: PAGE_SIZE }),
    enabled: hasToken,
  });

  const groups = data?.data.content ?? [];
  const pageData = data?.data;

  const handleSearch = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setKeyword(searchInput.trim());
    setPage(0);
  };

  return (
    <section className="group-page">
      <div className="group-toolbar">
        <div className="group-title-block">
          <h1>스터디 그룹</h1>
          <p>관심 있는 주제의 그룹을 찾고 함께 공부해보세요.</p>
        </div>
        {user && (
          <Link to="/groups/new" className="button button-primary">
            그룹 만들기
          </Link>
        )}
      </div>

      {!hasToken && (
        <div className="empty-state">
          로그인 후 스터디 그룹을 조회할 수 있습니다.
          <div className="group-form-actions">
            <Link to="/login" className="button button-primary">
              로그인
            </Link>
          </div>
        </div>
      )}

      {hasToken && (
        <form className="group-search-form" onSubmit={handleSearch}>
        <input
          value={searchInput}
          onChange={(event) => setSearchInput(event.target.value)}
          placeholder="그룹명, 설명, 카테고리로 검색"
        />
        <button type="submit" className="button button-primary">
          검색
        </button>
        </form>
      )}

      {hasToken && isLoading && <div className="loading-state">그룹을 불러오는 중입니다.</div>}
      {hasToken && isError && <div className="error-state">그룹 목록을 불러오지 못했습니다.</div>}

      {hasToken && !isLoading && !isError && groups.length === 0 && (
        <div className="empty-state">조건에 맞는 스터디 그룹이 없습니다.</div>
      )}

      {hasToken && (
        <div className="group-grid">
        {groups.map((group) => (
          <Link to={`/groups/${group.id}`} className="group-card" key={group.id}>
            <span className="group-card-category">{group.category}</span>
            <h2>{group.name}</h2>
            <p>{group.description}</p>
            <div className="group-card-meta">
              <span>
                {group.currentMemberCount}/{group.maxMemberCount}명
              </span>
              <span>{new Date(group.createdAt).toLocaleDateString()}</span>
            </div>
          </Link>
        ))}
        </div>
      )}

      {hasToken && pageData && pageData.totalPages > 1 && (
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

export default GroupListPage;
