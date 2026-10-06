import { Page } from '@playwright/test';

export async function loginAs(page: Page, role: 'customer' | 'underwriter' | 'claims' | 'caretaker' | 'admin') {
  await page.goto('/auth/login');
  const roleButtons: Record<string, string> = {
    customer: 'Customer',
    underwriter: 'Underwriter',
    claims: 'Claims Adjuster',
    caretaker: 'Caretaker',
    admin: 'Admin'
  };
  await page.getByRole('button', { name: roleButtons[role] }).click();
  await page.locator('#login-submit-button').click();
  const targetUrls: Record<string, RegExp> = {
    customer: /.*\/customer/,
    underwriter: /.*\/underwriter/,
    claims: /.*\/claims/,
    caretaker: /.*\/caretaker/,
    admin: /.*\/admin/
  };
  await page.waitForURL(targetUrls[role], { timeout: 15000 });
}
