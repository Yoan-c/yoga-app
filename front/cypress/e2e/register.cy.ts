describe('Register test e2e', () => {
  
  it('should register a user', () => {
    cy.visit('/register')
    cy.intercept('POST', 'api/auth/register', [])
    
    cy.get('input[formControlName="firstName"]').type('Jean')
    cy.get('input[formControlName="lastName"]').type('Dupond')
    cy.get('input[formControlName="email"]').type('JD@test.com')
    cy.get('input[formControlName="password"]').type('test!123')

    cy.get('button[type="submit"').click()
  })

  it('should not register a user', () => {
    cy.visit('/register')
    
    cy.get('input[formControlName="firstName"]').type('Jean')
    cy.get('input[formControlName="lastName"]').type('Dupond')
    cy.get('input[formControlName="email"]').type('JD@test.com')
    cy.get('input[formControlName="password"]').type('test!123')

    cy.get('button[type="submit"').click()

    cy.get('span').contains('An error occurred')
  })
  
})