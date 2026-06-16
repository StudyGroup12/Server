import { useEffect, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link, useParams } from 'react-router-dom';
import { fetchGroupDetail } from '../../api/group.api';
import {
  useStartStudySession,
  useStopStudySession,
  useTimerStatus,
} from '../../hooks/useStudyTimer';
import { getApiErrorMessage } from '../../utils/apiError';
import { formatMinutes, formatStopwatch } from '../../utils/duration';
import '../stats/dashboard.css';

const StudyTimerPage = () => {
  const { groupId } = useParams();
  const numericGroupId = Number(groupId);
  const hasToken = !!sessionStorage.getItem('accessToken');

  const statusQuery = useTimerStatus(numericGroupId);
  const groupQuery = useQuery({
    queryKey: ['group', numericGroupId],
    queryFn: () => fetchGroupDetail(numericGroupId),
    enabled: hasToken && Number.isFinite(numericGroupId),
  });
  const startMutation = useStartStudySession(numericGroupId);
  const stopMutation = useStopStudySession(numericGroupId);

  const status = statusQuery.data?.data;
  const running = status?.running ?? false;
  const baseElapsed = status?.elapsedSeconds ?? 0;

  // 서버가 내려준 경과 초를 기준점으로, 클라이언트는 상대 시간만 더해 카운트한다.
  // (절대 시각을 파싱하지 않으므로 서버-브라우저 타임존 차이의 영향을 받지 않음)
  const [elapsedSeconds, setElapsedSeconds] = useState(0);
  useEffect(() => {
    if (!running) {
      setElapsedSeconds(0);
      return;
    }
    const startedTickMs = Date.now();
    const tick = () =>
      setElapsedSeconds(baseElapsed + Math.floor((Date.now() - startedTickMs) / 1000));
    tick();
    const id = window.setInterval(tick, 1000);
    return () => window.clearInterval(id);
  }, [running, baseElapsed]);

  if (!hasToken) {
    return <div className="study-empty">로그인 후 학습 타이머를 이용할 수 있습니다.</div>;
  }

  if (statusQuery.isLoading) {
    return <div className="study-empty">타이머 정보를 불러오는 중입니다.</div>;
  }

  const groupName = groupQuery.data?.data.name ?? '스터디';

  const handleStart = () => {
    startMutation.mutate(undefined, {
      onError: (error) => alert(getApiErrorMessage(error, '학습 시작에 실패했습니다.')),
    });
  };

  const handleStop = () => {
    stopMutation.mutate(undefined, {
      onError: (error) => alert(getApiErrorMessage(error, '학습 종료에 실패했습니다.')),
    });
  };

  return (
    <section className="study-page">
      <div className="study-page-header">
        <div>
          <h1>{groupName} · 학습 타이머</h1>
          <p>공부를 시작하면 학습 시간이 자동으로 누적되고 그룹 랭킹에 반영됩니다.</p>
        </div>
        <div className="study-page-actions">
          <Link to={`/groups/${numericGroupId}`} className="button">
            그룹으로
          </Link>
          <Link to={`/groups/${numericGroupId}/stats`} className="button">
            학습 통계
          </Link>
        </div>
      </div>

      <div className="timer-box">
        <div className={`timer-display${running ? ' running' : ''}`}>
          {formatStopwatch(elapsedSeconds)}
        </div>
        <div className="timer-state">
          {running ? '학습 진행 중입니다.' : '학습을 시작해 보세요.'}
        </div>
        {running ? (
          <button
            type="button"
            className="button button-danger timer-button"
            onClick={handleStop}
            disabled={stopMutation.isPending}
          >
            학습 종료
          </button>
        ) : (
          <button
            type="button"
            className="button button-primary timer-button"
            onClick={handleStart}
            disabled={startMutation.isPending}
          >
            학습 시작
          </button>
        )}
      </div>

      <div className="summary-grid">
        <div className="summary-card">
          <span className="summary-label">오늘 학습</span>
          <span className="summary-value">{formatMinutes(status?.todayMinutes ?? 0)}</span>
        </div>
        <div className="summary-card">
          <span className="summary-label">이번 주 학습</span>
          <span className="summary-value">{formatMinutes(status?.weekMinutes ?? 0)}</span>
        </div>
        <div className="summary-card">
          <span className="summary-label">누적 학습</span>
          <span className="summary-value">{formatMinutes(status?.totalMinutes ?? 0)}</span>
        </div>
      </div>
    </section>
  );
};

export default StudyTimerPage;
