describe('Session admin test e2e', () => {

  const mockDateSession = '2023-12-14'
  const mockTeacher = [
    {  
      id: 1,
      lastName: "Fabienne",
      firstName: "Dupond",
      createdAt: new Date(),
      updatedAt: new Date()
    },
    { 
      id: 2,
      lastName: "Virginie",
      firstName: "Delanoe",
      createdAt: new Date(),
      updatedAt: new Date(),
    },
  ];

  const mockSession = [{
    id: 1,
    name: "Session Zen",
    description: "Session de Zen attitude",
    date: mockDateSession,
    teacher_id: 1,
    users: [1],
    createdAt: mockDateSession,
    updatedAt: mockDateSession,
  }]

  beforeEach(() => {
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    })
  })

  beforeEach(() => {

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      [])
    .as('session')

    cy.intercept(
      {
        method: 'POST',
        url: '/api/session',
      },
      [])
    .as('sessionPost')



    cy.intercept('GET', 'api/session/1', {
      body: mockSession[0],
    })

    cy.intercept('PUT', 'api/session/1', {
      body: mockSession[0],
    })

    cy.intercept('delete', 'api/session/1', {
      body: {},
    })
  })

  beforeEach(() => {
    cy.intercept('GET', 'api/teacher', {
      body: mockTeacher,
    })

    cy.intercept('GET', 'api/teacher/1', {
      body: mockTeacher[0],
    })
  
  })


  it('Should login as an admin and create edit update and delete a session', () => {

    cy.visit('/login')

    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/sessions')
    cy.get('button[routerLink="create"]').contains('Create').as('btnCreate')
    cy.get('@btnCreate').click()

    cy.intercept('GET', 'api/session', {
      body: mockSession,
    })

    cy.get('input[formControlName="name"]').type('Session').as('nameSession')
    cy.get('input[formControlName="date"]').type(mockDateSession)
    cy.get('mat-select[formControlName="teacher_id"]').click().as('teatcherSelect');
    cy.get('mat-option').contains('Delanoe Virginie').click()
    cy.get('textarea[formControlName="description"]').type("Session de Zen attitude")
    cy.get('button[type="submit"]').contains('Save').click().as('btnSave')

    cy.url().should('include', "/sessions")

    cy.get('button').contains('Edit').as('btnEdit')
    cy.get('@btnEdit').click()

    cy.get('@teatcherSelect').click()
    cy.get('mat-option').contains('Dupond Fabienne').click()
    cy.get('button[type="submit"]').contains('Save').click()
  
    
    cy.get('button').contains('Detail').click().as('btnDetail')
    cy.get('span').contains('Dupond FABIENNE')
    cy.go('back');

    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      [])
    .as('session')

    cy.get('@btnDetail').click()
    cy.get('button').contains('Delete').click()

  })
  
})