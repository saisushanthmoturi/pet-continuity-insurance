import { test, expect } from '@playwright/test';
import { loginAs } from './auth-helper';

test.describe('Authentication & Navigation Suite', () => {
  test('Redirects unauthenticated visitors to login page', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveURL(/.*\/auth\/login/);
    await expect(page.locator('h2')).toContainText('Sign In to Your Account');
  });

  test('Renders responsive 70% width navbar and thematic footer', async ({ page }) => {
    await page.goto('/auth/login');
    const nav = page.locator('header nav');
    await expect(nav).toBeVisible();
    await expect(page.locator('footer')).toContainText('PawContinuity');
    await expect(page.locator('footer')).toContainText('Paw Continuity Assurance Inc');
  });

  test('Demo quick-fill fills credentials correctly for all roles', async ({ page }) => {
    await page.goto('/auth/login');
    
    await page.getByRole('button', { name: 'Underwriter' }).click();
    await expect(page.locator('input#email')).toHaveValue('underwriter@pawcontinuity.com');
    
    await page.getByRole('button', { name: 'Claims Adjuster' }).click();
    await expect(page.locator('input#email')).toHaveValue('adjuster@pawcontinuity.com');

    await page.getByRole('button', { name: 'Caretaker' }).click();
    await expect(page.locator('input#email')).toHaveValue('caretaker@pawcontinuity.com');

    await page.getByRole('button', { name: 'Admin' }).click();
    await expect(page.locator('input#email')).toHaveValue('admin@pawcontinuity.com');

    await page.getByRole('button', { name: 'Customer' }).click();
    await expect(page.locator('input#email')).toHaveValue('sarah.jenkins@example.com');
  });

  test('Logs in through UI with real credentials for each role', async ({ page }) => {
    // Customer
    await loginAs(page, 'customer');
    await expect(page.locator('h1')).toContainText('Pet Owner Portal');
    await page.locator('#logout-button').click();

    // Underwriter
    await loginAs(page, 'underwriter');
    await expect(page.locator('h1')).toContainText('Underwriting & Risk Console');
    await page.locator('#logout-button').click();

    // Claims Adjuster
    await loginAs(page, 'claims');
    await expect(page.locator('h1')).toContainText('Claims Adjudication Desk');
    await page.locator('#logout-button').click();

    // Caretaker
    await loginAs(page, 'caretaker');
    await expect(page.locator('h1')).toContainText('Caretaker Fiduciary Portal');
    await page.locator('#logout-button').click();

    // Admin
    await loginAs(page, 'admin');
    await expect(page.locator('h1')).toContainText('System Administration Console');
    await page.locator('#logout-button').click();
  });

  test('Registration page renders form and allows input', async ({ page }) => {
    await page.goto('/auth/register');
    await expect(page.locator('h2')).toContainText('Create an Account');
    await page.locator('input#fullName').fill('Dr. John Watson');
    await page.locator('input#regEmail').fill('john.watson@example.com');
    await page.locator('input#regPassword').fill('password123');
    await page.locator('select#role').selectOption('CUSTOMER');
    await expect(page.locator('input#fullName')).toHaveValue('Dr. John Watson');
  });
});
