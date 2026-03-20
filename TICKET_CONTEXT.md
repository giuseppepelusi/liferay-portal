# LPD-79025: Apostrophe in address selector during checkout is not formatted

## Summary
The address selector in the checkout process displays apostrophes in address names as `&#39;` instead of a literal apostrophe. This is likely due to double escaping in the JSP.

## Files to modify
- `modules/apps/commerce/commerce-checkout-web/src/main/resources/META-INF/resources/checkout_step/address.jsp`

## Analysis
The `aui:option` tag in `address.jsp` uses `HtmlUtil.escape(commerceAddress.getName())` for the `label` attribute.
It also uses `HtmlUtil.escapeAttribute(...)` for `data-*` attributes.
Since `aui` tags in Liferay typically perform their own escaping, this leads to double-escaping of characters like the apostrophe.

## Solution
Remove unnecessary `HtmlUtil.escape()` and `HtmlUtil.escapeAttribute()` calls from the `aui:option` tag in `address.jsp`.
