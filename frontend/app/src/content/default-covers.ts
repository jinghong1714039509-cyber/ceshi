export const defaultCoverImages = [
  '/covers/campus-01.svg',
  '/covers/campus-02.svg',
  '/covers/campus-03.svg',
  '/covers/campus-04.svg',
  '/covers/campus-05.svg',
  '/covers/campus-06.svg',
  '/covers/campus-07.svg',
  '/covers/campus-08.svg',
]

export function pickDefaultCover(seed: string) {
  const normalized = Array.from(seed).reduce((acc, char) => acc + char.charCodeAt(0), 0)
  return defaultCoverImages[normalized % defaultCoverImages.length]
}
