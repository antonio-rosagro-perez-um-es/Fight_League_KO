# Pending Frontend Requirements

This file tracks requirements from `ia/frontend.md` that are not fully implemented and why they are blocked or deferred.

## Pending

### Admin Fighter Media Management

- Requirement: `ia/frontend.md` asks admin fighter rows to include controls for updating banner, portrait, and icon media.
- Status: Not implemented.
- Reason: The backend currently manages fighter data fields but does not expose a media upload/replacement endpoint or file-management workflow.
- Needed: Define whether media updates should be frontend-only asset path edits, local file replacement, or backend-supported uploads.

### Final Reference-Level Visual Polish

- Requirement: Several views reference external visual targets such as Street Fighter and 2XKO-style pages.
- Status: Partially implemented.
- Reason: Layouts are functional and responsive, but exact reference-level polish depends on final design decisions and complete media quality.
- Needed: Final visual pass after all assets and UX decisions are stable.

## Not Pending

- Root `assets/` pipeline is wired through `frontend/angular.json`.
- Brand logo and favicon are available.
- Fighter portrait/banner/icon naming is standardized.
- Warwick portrait typo is fixed.
- Searchable fighter/fuse asset dropdowns are implemented.
- Control glyph rendering is implemented with text mode toggle and invalid-token fallback chips.
- Strict combo notation validation is implemented in combo create/edit forms.
- Personal statistics are implemented.
- Admin statistics are implemented as read-only because backend has calculated stats, not editable stats CRUD.
- Tournament winner changes are implemented with stat reversal.
- Custom CSS bracket is intentionally used instead of an external library.
