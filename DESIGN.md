# Claude-Inspired Frontend Rules

This project uses a Claude-inspired visual system for the frontend only.
Backend APIs, route semantics, and data contracts must remain unchanged.

## Product Direction

- Tone: calm, warm, editorial, campus-community-first
- Feel: closer to a student community product than a generic SaaS dashboard
- Interface posture: readable, grounded, human, not "AI template"
- Visual emphasis: content, activities, clubs, conversations

## Core Principles

1. Prefer readable layouts over decorative complexity.
2. Use clear information hierarchy without stacking too many helper labels.
3. Keep cards and panels quiet; content should carry the page.
4. Avoid blue enterprise dashboard styling.
5. Avoid oversized radii, heavy blur, glass panels, and startup-gradient hero noise.
6. Do not introduce new frontend abstractions unless they reduce repeated product logic.

## Color System

- Paper background: `#fcfbf8`
- Soft paper: `#f7f4ef`
- Surface: `#fffdf9`
- Surface muted: `#f4efe7`
- Ink text: `#2c2a28`
- Soft text: `#5f5954`
- Border: `#e6ddd1`
- Border strong: `#d6cabd`
- Primary terracotta: `#d97757`
- Primary deep: `#b85c38`
- Primary soft: `#f3e1d7`
- Sage accent: `#7a8471`
- Sage soft: `#e7ebe3`
- Gold accent: `#c7a15a`
- Danger: `#bf5b53`

## Typography

- Heading stack: `Charter, Georgia, Cambria, "Times New Roman", serif`
- UI/body stack: `Inter, "Noto Sans SC", "Segoe UI", sans-serif`
- Mono/detail stack: `"SFMono-Regular", "JetBrains Mono", Consolas, monospace`

Rules:

- Titles should be serif-led and compact.
- Body text should be sans and easy to scan.
- Avoid too many all-caps labels.
- Avoid subtitle clutter under every heading.

## Spacing And Shape

- Base spacing: 8px system
- Standard radius: 8px
- Large panel radius: 12px to 16px only when necessary
- Do not use 24px+ radius on ordinary cards
- Use generous whitespace around reading surfaces

## Surfaces And Depth

- Use paper-like surfaces with visible warm borders.
- Shadows must stay soft and shallow.
- Preferred shadow:
  - `0 10px 30px rgba(62, 45, 35, 0.06)`
  - `0 2px 8px rgba(62, 45, 35, 0.04)`
- Avoid glassmorphism and strong backdrop blur.

## Layout Patterns

- Main shell should feel like a reading workspace, not an admin console.
- Sidebar is a quiet navigation rail with minimal supporting text.
- Top bar should be light and mostly functional.
- Public pages should foreground activity cards and community content.
- Detail pages should use article-like content blocks with side actions.

## Component Rules

### Buttons

- Primary: terracotta fill with deep terracotta hover
- Secondary: ivory surface with warm border
- Text buttons: restrained, no excessive color saturation

### Cards

- Cards must communicate a single purpose.
- Do not stack too many nested wrappers.
- Activity and post cards should privilege image, title, time, location, and state.

### Forms

- Keep forms compact and direct.
- Label only what is necessary.
- Upload actions should have clear previews and fallback states.

### Navigation

- Keep nav labels short.
- Do not place explanatory sentences under nav items.
- User identity, login state, and account actions belong in the avatar popover.

## Content Behavior

- Public browsing centers on activities and selected forum content.
- Logged-in students can browse activities, clubs, forum, club spaces, and messages.
- Managers and teacher-admin users get overview/workspace capabilities.
- Activity completion flow stays visible in detail pages with image evidence and review comments.

## Anti-Patterns To Avoid

- Generic dashboard gradients
- Empty hero slogans that do not help the task
- Repeated "overview / insights / workspace" blocks without product meaning
- Over-componentized shells for simple static sections
- Template naming such as `hero-card`, `feature-card`, `showcase-panel` everywhere
- Decorative helper copy under every title and nav item

## Refactor Guardrails

- Do not change backend request payloads or endpoint paths.
- Do not change routing semantics unless the route already exists.
- Prefer rewriting noisy pages fully in clean UTF-8 when files contain encoding corruption.
- Every changed page should look like it belongs to the same warm paper system.
