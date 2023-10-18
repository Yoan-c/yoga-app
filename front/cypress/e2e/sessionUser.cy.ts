describe('Session user test e2e', () => {
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
    users: [0],
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
        admin: false
      },
    })
  })

  beforeEach(() => {

    cy.intercept('POST', '/api/session/1/participate/1', {
      body: 'Participation successful',
      statusCode: 200,
    }).as('participationRequest');
  })

  beforeEach(() => {
    cy.intercept('GET', 'api/teacher', {
      body: mockTeacher,
    })

    cy.intercept('GET', 'api/teacher/1', {
      body: mockTeacher[0],
    })
  })


  it('Should login as user and participate or unparticipate to a session', () => {

    cy.intercept('GET', 'api/session/1', {
      body: mockSession[0],
    }).as('firstSession')

    cy.intercept('GET', 'api/session', {
      body: mockSession,
    })

    cy.visit('/login')

    cy.get('input[formControlName=email]').type("Yoan@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    cy.url().should('include', '/sessions')

    cy.get('button').contains('Detail').click()


    cy.intercept('GET', 'api/session/1', ((req) => {
      mockSession[0].users[0] = 1
      req.body =  mockSession[0]
    }))

    cy.url().should('include', '/sessions/detail/1')
    
 
    cy.get('button').contains('Participate').click()

  })
  
})