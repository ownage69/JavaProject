import { SurfaceCard } from './SurfaceCard';

export function LoadingState({
  title = 'Loading your library workspace...',
}: {
  title?: string;
}) {
  return (
    <SurfaceCard className="loading-state">
      <div className="loading-state__shelf">
        <span />
        <span />
        <span />
      </div>
      <h3>{title}</h3>
      <p>Please wait a moment while the interface gathers the latest records.</p>
    </SurfaceCard>
  );
}
