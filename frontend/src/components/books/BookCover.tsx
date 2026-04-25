const coverTones = ['forest', 'burgundy', 'ocean', 'gold'] as const;

function resolveTone(title: string) {
  const index = [...title].reduce((sum, character) => sum + character.charCodeAt(0), 0);
  return coverTones[index % coverTones.length];
}

interface BookCoverProps {
  title: string;
  coverUrl?: string | null;
}

export function BookCover({ title, coverUrl }: BookCoverProps) {
  const initials = title
    .split(' ')
    .slice(0, 2)
    .map((chunk) => chunk[0] || '')
    .join('')
    .toUpperCase();

  if (coverUrl) {
    return (
      <div className="book-cover book-cover--image">
        <img src={coverUrl} alt={`${title} cover`} className="book-cover__image" />
      </div>
    );
  }

  return (
    <div className={`book-cover book-cover--${resolveTone(title)}`}>
      <small>Catalog edition</small>
      <strong>{initials}</strong>
      <span>{title}</span>
    </div>
  );
}
